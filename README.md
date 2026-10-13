# Scholarship-portal
This is a website to check the statuses of all those scholarships (whether they are approved or rejected) and the list of available scholarships as well.

## Structure
| Folder | What | Details |
|---|---|---|
| [`backend/`](backend/) | Spring Boot 3 REST API (Java 17, JPA, MySQL / PostgreSQL) | [backend/README.md](backend/README.md) |
| [`frontend/`](frontend/) | React (Vite) app for students and admins | [frontend/README.md](frontend/README.md) |

## Run locally
1. Backend: start MySQL, then in `backend/` run `./mvnw spring-boot:run` (Windows: `.\mvnw.cmd spring-boot:run`). API at http://localhost:8080/api
2. Frontend: in `frontend/` run `npm install` then `npm run dev`. App at http://localhost:5173

Default admin login: `admin@scholarship.com` / `admin123` (set `ADMIN_PASSWORD` before deploying).

## Deploy
- Backend on Render: New > Web Service > this repository, set **Root Directory** to `backend` (it uses the `Dockerfile`). Environment variables are listed in [backend/README.md](backend/README.md).
- Frontend on Vercel or Netlify: import this repository, set **Root Directory** / base directory to `frontend`, build `npm run build`, output `dist`, and set `VITE_API_URL` to the backend address.

## CI
GitHub Actions runs `.github/workflows/backend.yml` (build + API tests against PostgreSQL) when `backend/` changes,
and `.github/workflows/frontend.yml` (install + build) when `frontend/` changes.
