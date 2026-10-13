# Scholarship-portal
This is a website to check the statuses of all those scholarships (whether they are approved or rejected) and the list of available scholarships as well.

## Structure
| Folder | What | Details |
|---|---|---|
| [`backend/`](backend/) | Spring Boot 3 REST API (Java 17, JPA, MySQL / PostgreSQL, login tokens) | [backend/README.md](backend/README.md) |
| [`frontend/`](frontend/) | React (Vite) app for students and admins | [frontend/README.md](frontend/README.md) |
| `Dockerfile` | Builds both into one service: the website and the API share one address | |
| `deploy/` | Docker Compose stack for your own server (database, app, HTTPS) and its setup script | |
| `render.yaml` | Render Blueprint, if you prefer Render to your own server | |

## Go live with Coolify (if your server runs Coolify)
Coolify builds the root `Dockerfile` (website + API in one service) and gives it HTTPS.
1. **Database:** Project > **+ New** > **Database** > **PostgreSQL** > **Start**. Copy its **Postgres URL (internal)**.
2. **App:** same project > **+ New** > **Private Repository (with GitHub App)** (create the GitHub App when asked and give it
   access to this repository) > pick `Scholarship-portal`, branch `main`.
   - Build Pack: **Dockerfile**, Base Directory `/`, Dockerfile location `/Dockerfile`
   - Ports Exposes: **8080**
3. **Environment Variables** of the app:
   - `DATABASE_URL` = the internal Postgres URL from step 1 (the `postgres://...` form works as is)
   - `ADMIN_PASSWORD` = the admin login password
   - `JWT_SECRET` = any long random text (keeps people logged in across redeploys)
4. **Domains:** keep the generated `http://....sslip.io` address, or change `http` to `https` for a certificate.
5. **Deploy**. The first build takes a few minutes; logs are under **Deployments**.

Health check (optional): path `/api/scholarships`, port 8080.
Coolify redeploys on every push to `main` when its GitHub App is connected.

## Go live on your own server (VPS)
Every push to `main` deploys automatically: GitHub builds the app, copies it to the server over SSH and restarts it
(`.github/workflows/deploy.yml`). The server runs PostgreSQL, the app and Caddy (free HTTPS certificate) with Docker.

Needs: a Linux VPS (Ubuntu/Debian) with about 1 GB of memory free, SSH access as root or a user with passwordless sudo.

One-time setup:
1. On your computer, make a key just for deploys: `ssh-keygen -t ed25519 -f scholarship-deploy -N ""`
2. Put the public key on the server: `ssh-copy-id -i scholarship-deploy.pub USER@SERVER-IP`
3. On GitHub: this repository > **Settings > Secrets and variables > Actions > New repository secret**, and add
   - `VPS_HOST` - the server IP
   - `VPS_USER` - the SSH user (for example `root`)
   - `VPS_SSH_KEY` - the whole content of the private key file `scholarship-deploy`
   - `ADMIN_PASSWORD` - the admin login password (optional; otherwise one is generated and saved on the server)
   - `DOMAIN` - your domain, for example `scholarships.example.com` (optional, see below)
   - `VPS_PORT` - only if SSH is not on port 22
4. **Actions** tab > **Deploy to VPS** > **Run workflow**. The last log line shows the address, for example
   `LIVE: https://203.0.113.10.sslip.io`.

Domain: without `DOMAIN` the site uses `<server-ip>.sslip.io`, which works with HTTPS right away.
For your own domain, first add a DNS **A record** pointing it to the server IP, then set the `DOMAIN` secret.
`DOMAIN` and `ADMIN_PASSWORD` are read only on the first deploy; later they live in `~/scholarship-portal/.env` on the
server (edit that file and run `bash ~/scholarship-portal/setup.sh up` to change them).

If the server already runs nginx or Apache on ports 80/443, Caddy is skipped and the app listens on
`http://127.0.0.1:8080`; the deploy log prints the nginx line to forward your domain to it.

Useful commands on the server (in `~/scholarship-portal`): `sudo docker compose logs -f app`, `sudo docker compose ps`.
Backup the database: `sudo docker compose exec db pg_dump -U scholarship scholarship_db > backup.sql`

## Other ways to go live
- **Render** (no server needed): Render dashboard > New > Blueprint > this repository; it reads `render.yaml` and asks for
  `ADMIN_PASSWORD`. Free plan: sleeps when idle and its free database expires after 30 days.
- **Vercel for the website**: import the repository, Root Directory `frontend`, env `VITE_API_URL=https://YOUR-API/api`;
  set `FRONTEND_URL` on the API to the website's address.

## Run locally
1. Backend: start MySQL, then in `backend/` run `./mvnw spring-boot:run` (Windows: `.\mvnw.cmd spring-boot:run`). API at http://localhost:8080/api
2. Frontend: in `frontend/` run `npm install` then `npm run dev`. App at http://localhost:5173

Local admin login: `admin@scholarship.com` / `admin123`.

## CI
- `deploy.yml` deploys to your server on every push to `main` (skipped until the `VPS_HOST` secret is set).
- `docker.yml` builds the production image, starts it with PostgreSQL and runs the API tests (every push).
- `backend.yml` builds the API and runs `backend/ci/api-test.sh` (when `backend/` changes).
- `frontend.yml` installs and builds the React app (when `frontend/` changes).
