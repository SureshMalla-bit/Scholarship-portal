#!/bin/sh
# Starts the backend inside Docker. Spring needs one JDBC address (DB_URL); it is built here from either
#  - DATABASE_URL like postgres://user:password@host:5432/dbname (what Coolify, Heroku and others show), or
#  - DB_HOST / DB_PORT / DB_NAME (what render.yaml and deploy/docker-compose.yml pass).
if [ -z "$DB_URL" ] && [ -n "$DATABASE_URL" ]; then
  rest="${DATABASE_URL#*://}"            # user:password@host:5432/dbname?options
  creds="${rest%@*}"                     # user:password (split on the last @)
  location="${rest##*@}"                 # host:5432/dbname?options
  export DB_USERNAME="${DB_USERNAME:-${creds%%:*}}"
  export DB_PASSWORD="${DB_PASSWORD:-${creds#*:}}"
  export DB_URL="jdbc:postgresql://${location}"
fi
if [ -z "$DB_URL" ] && [ -n "$DB_HOST" ]; then
  export DB_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT:-5432}/${DB_NAME}"
fi
# Keeps memory use inside Render's free 512 MB instance
exec java -XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Xss512k -jar app.jar
