#!/usr/bin/env bash
# Calls the main API endpoints and stops at the first wrong answer.
# Usage: bash ci/api-test.sh http://localhost:8080/api
set -euo pipefail
API=${1:-http://localhost:8080/api}
J='Content-Type: application/json'
pass() { echo "PASS  $1"; }
fail() { echo "FAIL  $1"; exit 1; }
code() { curl -s -o /tmp/body -w "%{http_code}" "$@"; }

[ "$(curl -s $API/scholarships | jq length)" -ge "${MIN_SCHOLARSHIPS:-9}" ] && pass "sample scholarships loaded" || fail "sample scholarships"

[ "$(code -X POST $API/auth/login -H "$J" -d '{"email":"admin@scholarship.com","password":"admin123"}')" = 200 ] \
  && [ "$(jq -r .role /tmp/body)" = ADMIN ] && pass "admin login" || fail "admin login"
[ "$(jq -r '.password // "hidden"' /tmp/body)" = hidden ] && pass "password not returned" || fail "password leaked"
[ "$(code -X POST $API/auth/login -H "$J" -d '{"email":"admin@scholarship.com","password":"wrong"}')" != 200 ] \
  && pass "wrong password rejected" || fail "wrong password accepted"

S='{"name":"Test Student","email":"student@test.com","password":"pass123","gender":"Male","category":"SC","phone":"9999999999","course":"B.Tech ECE"}'
[ "$(code -X POST $API/auth/register -H "$J" -d "$S")" = 201 ] && pass "student register" || { cat /tmp/body; fail "student register"; }
SID=$(jq -r .id /tmp/body)
[ "$(code -X POST $API/auth/register -H "$J" -d "$S")" != 201 ] && pass "duplicate email rejected" || fail "duplicate email"
[ "$(code -X POST $API/auth/login -H "$J" -d '{"email":"student@test.com","password":"pass123"}')" = 200 ] \
  && [ "$(jq -r .role /tmp/body)" = STUDENT ] && pass "student login" || fail "student login"

# Admin CRUD on scholarships
NEW='{"name":"CI Test Scholarship","provider":"CI","level":"UG","amount":"Rs 1,000","deadline":"2099-12-31","gender":"All","categories":"All","description":"test"}'
[ "$(code -X POST $API/scholarships -H "$J" -d "$NEW")" = 201 ] && pass "add scholarship" || { cat /tmp/body; fail "add scholarship"; }
NID=$(jq -r .id /tmp/body)
[ "$(code -X PUT $API/scholarships/$NID -H "$J" -d "${NEW/Rs 1,000/Rs 2,000}")" = 200 ] \
  && [ "$(jq -r .amount /tmp/body)" = "Rs 2,000" ] && pass "edit scholarship" || fail "edit scholarship"

# Student applies, edits, admin approves
PRAGATI=$(curl -s $API/scholarships | jq '[.[] | select(.gender=="Female")][0].id')
A='{"studentId":'$SID',"scholarshipId":'$NID',"course":"B.Tech ECE","percentage":82.5,"annualIncome":200000}'
[ "$(code -X POST $API/applications -H "$J" -d "$A")" = 201 ] && pass "apply" || { cat /tmp/body; fail "apply"; }
AID=$(jq -r .id /tmp/body)
[ "$(code -X POST $API/applications -H "$J" -d "$A")" = 409 ] && pass "second application blocked" || fail "duplicate application"
[ "$(code -X POST $API/applications -H "$J" -d '{"studentId":'$SID',"scholarshipId":'$PRAGATI',"course":"B.Tech","percentage":80,"annualIncome":1}')" = 400 ] \
  && pass "gender rule (male student, female-only scholarship)" || fail "gender rule"
[ "$(code -X PUT $API/applications/$AID -H "$J" -d "${A/82.5/90}")" = 200 ] && pass "edit pending application" || fail "edit application"
[ "$(curl -s $API/applications/student/$SID | jq length)" = 1 ] && pass "student sees own application" || fail "own applications"
[ "$(code -X PATCH "$API/applications/$AID/status?value=APPROVED")" = 200 ] \
  && [ "$(jq -r .status /tmp/body)" = APPROVED ] && pass "admin approves" || fail "approve"
[ "$(curl -s $API/applications | jq length)" -ge 1 ] && pass "admin sees all applications" || fail "all applications"

# Student profile update, admin student CRUD, deletes
U='{"name":"Test Student 2","email":"student@test.com","password":"","gender":"Male","category":"SC","phone":"8888888888","course":"B.Tech ECE"}'
[ "$(code -X PUT $API/students/$SID -H "$J" -d "$U")" = 200 ] && [ "$(jq -r .name /tmp/body)" = "Test Student 2" ] \
  && pass "update profile" || { cat /tmp/body; fail "update profile"; }
[ "$(code -X POST $API/auth/login -H "$J" -d '{"email":"student@test.com","password":"pass123"}')" = 200 ] \
  && pass "old password still works after profile edit" || fail "password kept"
[ "$(curl -s $API/students | jq length)" -ge 1 ] && pass "admin lists students" || fail "list students"
[ "$(code -X DELETE $API/scholarships/$NID)" -lt 300 ] && pass "delete scholarship (and its applications)" || { cat /tmp/body; fail "delete scholarship"; }
[ "$(code -X DELETE $API/students/$SID)" -lt 300 ] && pass "delete student" || { cat /tmp/body; fail "delete student"; }
echo "All API checks passed."
