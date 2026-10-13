#!/usr/bin/env bash
# Runs on the VPS (called by .github/workflows/deploy.yml, or by hand).
#   setup.sh install  - installs Docker if it is missing
#   setup.sh up       - creates .env on the first run, then starts or updates the portal
# Optional on the first run: DOMAIN (default <server-ip>.sslip.io), ADMIN_PASSWORD (default: generated), APP_PORT (8080)
set -euo pipefail
cd "$(dirname "$0")"

SUDO=""
[ "$(id -u)" -eq 0 ] || SUDO="sudo -n"
docker_cmd() { $SUDO docker "$@"; }
port_in_use() { (exec 3<>"/dev/tcp/127.0.0.1/$1") 2>/dev/null; }
random() { head -c 48 /dev/urandom | base64 | tr -dc 'A-Za-z0-9' | head -c "$1"; }

install() {
  if command -v docker >/dev/null && docker_cmd compose version >/dev/null 2>&1; then
    echo "Docker is installed"
    return
  fi
  echo "Installing Docker..."
  curl -fsSL https://get.docker.com | $SUDO sh
  $SUDO systemctl enable --now docker
}

create_env() {
  local domain="${DOMAIN:-}" admin="${ADMIN_PASSWORD:-}" proxy=caddy note=""
  if [ -z "$domain" ]; then
    domain="$(curl -fsS4 https://api.ipify.org).sslip.io"
  fi
  if [ -z "$admin" ]; then
    admin="$(random 16)"
    note="(generated - see ADMIN_PASSWORD in $(pwd)/.env)"
  fi
  # Another web server already owns ports 80/443: do not start Caddy, point that server at the app instead
  if port_in_use 80 || port_in_use 443; then proxy=none; fi
  umask 077
  cat > .env <<ENV
DOMAIN=$domain
PROXY=$proxy
APP_PORT=${APP_PORT:-8080}
ADMIN_EMAIL=admin@scholarship.com
ADMIN_PASSWORD=$admin
POSTGRES_PASSWORD=$(random 32)
JWT_SECRET=$(random 48)
ENV
  echo "Created .env: domain $domain, HTTPS proxy: $proxy, admin password $note"
}

up() {
  [ -f .env ] || create_env
  set -a; . ./.env; set +a
  local profile=()
  [ "$PROXY" = caddy ] && profile=(--profile caddy)
  docker_cmd compose "${profile[@]}" up -d --remove-orphans
  docker_cmd image prune -f >/dev/null

  echo "Waiting for the app to start..."
  for _ in $(seq 1 90); do
    if curl -fsS "http://127.0.0.1:$APP_PORT/api/scholarships" >/dev/null 2>&1; then
      echo "App is running."
      if [ "$PROXY" = caddy ]; then
        echo "LIVE: https://$DOMAIN"
      else
        echo "Ports 80/443 are used by another web server. Forward your domain to http://127.0.0.1:$APP_PORT"
        echo "nginx example:  location / { proxy_pass http://127.0.0.1:$APP_PORT; proxy_set_header Host \$host; }"
      fi
      return
    fi
    sleep 2
  done
  echo "The app did not start. Last log lines:"
  docker_cmd compose logs --tail 80 app
  exit 1
}

case "${1:-up}" in
  install) install ;;
  up) up ;;
  *) echo "Usage: $0 install|up"; exit 1 ;;
esac
