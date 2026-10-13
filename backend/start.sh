#!/bin/sh
# Starts the backend inside Docker.
# render.yaml passes the database as DB_HOST / DB_PORT / DB_NAME; Spring needs one JDBC address, built here.
if [ -z "$DB_URL" ] && [ -n "$DB_HOST" ]; then
  export DB_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT:-5432}/${DB_NAME}"
fi
# Keeps memory use inside Render's free 512 MB instance
exec java -XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Xss512k -jar app.jar
