#!/usr/bin/env bash
# Calls the main API endpoints and stops at the first wrong answer.
# Usage: bash ci/api-test.sh http://localhost:8080/api
set -euo pipefail
API=${1:-http://localhost:8080/api}
J='Content-Type: application/json'
pass() { echo "PASS  $1"; }
fail() { echo "FAIL  $1"; exit 1; }
code() { curl -sk -o /tmp/body -w "%{http_code}" "$@"; }

# The server answers before it has finished adding the sample data, so wait for it (up to 60 seconds)
COUNT=0
for _ in $(seq 1 30); do
  COUNT=$(curl -sk $API/scholarships | jq length 2>/dev/null || echo 0)
  [ "$COUNT" -ge "${MIN_SCHOLARSHIPS:-9}" ] && break
  sleep 2
done
[ "$COUNT" -ge "${MIN_SCHOLARSHIPS:-9}" ] && pass "sample scholarships loaded" || fail "sample scholarships (found $COUNT)"

[ "$(code -X POST $API/auth/login -H "$J" -d "{\"email\":\"admin@scholarship.com\",\"password\":\"${ADMIN_PASSWORD:-admin123}\"}")" = 200 ] \
  && [ "$(jq -r .role /tmp/body)" = ADMIN ] && pass "admin login" || fail "admin login"
[ "$(jq -r '.password // "hidden"' /tmp/body)" = hidden ] && pass "password not returned" || fail "password leaked"
ADMIN="Authorization: Bearer $(jq -r .token /tmp/body)"
[ "$(code -X POST $API/auth/login -H "$J" -d '{"email":"admin@scholarship.com","password":"wrong"}')" != 200 ] \
  && pass "wrong password rejected" || fail "wrong password accepted"

S='{"name":"Test Student","email":"student@test.com","password":"pass123","gender":"Male","category":"SC","phone":"9999999999","course":"B.Tech ECE"}'
[ "$(code -X POST $API/auth/register -H "$J" -d "$S")" = 201 ] && pass "student register" || { cat /tmp/body; fail "student register"; }
SID=$(jq -r .id /tmp/body)
[ "$(code -X POST $API/auth/register -H "$J" -d "$S")" != 201 ] && pass "duplicate email rejected" || fail "duplicate email"
[ "$(code -X POST $API/auth/login -H "$J" -d '{"email":"student@test.com","password":"pass123"}')" = 200 ] \
  && [ "$(jq -r .role /tmp/body)" = STUDENT ] && pass "student login" || fail "student login"
STU="Authorization: Bearer $(jq -r .token /tmp/body)"

# Security: admin URLs need the admin's token, students only reach their own data
NEW='{"name":"CI Test Scholarship","provider":"CI","level":"UG","amount":"Rs 1,000","deadline":"2099-12-31","gender":"All","categories":"All","description":"test"}'
[ "$(code -X POST $API/scholarships -H "$J" -d "$NEW")" = 401 ] && pass "no token: add scholarship blocked" || fail "no token accepted"
[ "$(code $API/students -H "Authorization: Bearer forged.token.value")" = 401 ] && pass "forged token rejected" || fail "forged token"
[ "$(code -X POST $API/scholarships -H "$J" -H "$STU" -d "$NEW")" = 403 ] && pass "student cannot add scholarship" || fail "student added scholarship"
[ "$(code $API/students -H "$STU")" = 403 ] && pass "student cannot list students" || fail "student listed students"
[ "$(code $API/applications -H "$STU")" = 403 ] && pass "student cannot see all applications" || fail "student saw all applications"
[ "$(code $API/students/$((SID + 1000)) -H "$STU")" = 403 ] && pass "student cannot read another student" || fail "student read another student"

# Admin CRUD on scholarships
[ "$(code -X POST $API/scholarships -H "$J" -H "$ADMIN" -d "$NEW")" = 201 ] && pass "add scholarship" || { cat /tmp/body; fail "add scholarship"; }
NID=$(jq -r .id /tmp/body)
[ "$(code -X PUT $API/scholarships/$NID -H "$J" -H "$ADMIN" -d "${NEW/Rs 1,000/Rs 2,000}")" = 200 ] \
  && [ "$(jq -r .amount /tmp/body)" = "Rs 2,000" ] && pass "edit scholarship" || fail "edit scholarship"

# Student applies, edits, admin approves
PRAGATI=$(curl -sk $API/scholarships | jq '[.[] | select(.gender=="Female")][0].id')
A='{"studentId":'$SID',"scholarshipId":'$NID',"course":"B.Tech ECE","percentage":82.5,"annualIncome":200000}'
[ "$(code -X POST $API/applications -H "$J" -H "$STU" -d "$A")" = 201 ] && pass "apply" || { cat /tmp/body; fail "apply"; }
AID=$(jq -r .id /tmp/body)
[ "$(code -X POST $API/applications -H "$J" -H "$STU" -d "$A")" = 409 ] && pass "second application blocked" || fail "duplicate application"
[ "$(code -X POST $API/applications -H "$J" -H "$STU" -d '{"studentId":'$SID',"scholarshipId":'$PRAGATI',"course":"B.Tech","percentage":80,"annualIncome":1}')" = 400 ] \
  && pass "gender rule (male student, female-only scholarship)" || fail "gender rule"
[ "$(code -X PUT $API/applications/$AID -H "$J" -H "$STU" -d "${A/82.5/90}")" = 200 ] && pass "edit pending application" || fail "edit application"
[ "$(curl -sk $API/applications/student/$SID -H "$STU" | jq length)" = 1 ] && pass "student sees own application" || fail "own applications"
[ "$(code -X PATCH "$API/applications/$AID/status?value=APPROVED" -H "$STU")" = 403 ] && pass "student cannot approve" || fail "student approved"
[ "$(code -X PATCH "$API/applications/$AID/status?value=APPROVED" -H "$ADMIN")" = 200 ] \
  && [ "$(jq -r .status /tmp/body)" = APPROVED ] && pass "admin approves" || fail "approve"
[ "$(curl -sk $API/applications -H "$ADMIN" | jq length)" -ge 1 ] && pass "admin sees all applications" || fail "all applications"

# Student profile update, admin student CRUD, deletes
U='{"name":"Test Student 2","email":"student@test.com","password":"","gender":"Male","category":"SC","phone":"8888888888","course":"B.Tech ECE"}'
[ "$(code -X PUT $API/students/$SID -H "$J" -H "$STU" -d "$U")" = 200 ] && [ "$(jq -r .name /tmp/body)" = "Test Student 2" ] \
  && pass "update profile" || { cat /tmp/body; fail "update profile"; }
[ "$(code -X POST $API/auth/login -H "$J" -d '{"email":"student@test.com","password":"pass123"}')" = 200 ] \
  && pass "old password still works after profile edit" || fail "password kept"
[ "$(curl -sk $API/students -H "$ADMIN" | jq length)" -ge 1 ] && pass "admin lists students" || fail "list students"
[ "$(code -X DELETE $API/scholarships/$NID -H "$ADMIN")" -lt 300 ] && pass "delete scholarship (and its applications)" || { cat /tmp/body; fail "delete scholarship"; }
[ "$(code -X DELETE $API/students/$SID -H "$ADMIN")" -lt 300 ] && pass "delete student" || { cat /tmp/body; fail "delete student"; }
echo "All API checks passed."
