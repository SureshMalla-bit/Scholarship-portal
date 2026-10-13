# Scholarship Management System - Backend

Spring Boot REST API with MySQL. The React frontend is in `../frontend`.

## Tech
Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Bean Validation, BCrypt (spring-security-crypto), MySQL.

## Run on your laptop
1. Install JDK 17+, Maven and MySQL. Start MySQL.
2. In `src/main/resources/application.properties` check the MySQL username and password (default `root` / `root`).
   The database `scholarship_db` is created automatically.
3. Run `.\mvnw.cmd spring-boot:run` in Command Prompt inside this folder (no Maven install needed),
   or run `ScholarshipManagementApplication.java` from VS Code / IntelliJ.
4. The API runs at http://localhost:8080/api

On first start it creates:
- Admin login: `admin@scholarship.com` / `admin123` (change it with ADMIN_EMAIL / ADMIN_PASSWORD)
- Sample Indian scholarships (amounts follow the real schemes; deadlines are sample dates)

## Folder structure
    model/        User, Scholarship, ScholarshipApplication, Role, ApplicationStatus  (database tables)
    dto/          LoginRequest, UserRequest, ApplicationRequest  (data sent by the React forms)
    repository/   Spring Data JPA interfaces  (database queries)
    service/      Business rules  (deadline, gender, category, duplicate checks)
    controller/   REST endpoints
    exception/    Error messages returned as JSON
    config/       CORS, BCrypt, sample data

## REST API
| Method | URL | Who | Purpose |
|---|---|---|---|
| POST | /api/auth/register | Student | Create account |
| POST | /api/auth/login | Both | Log in, returns user with role |
| GET | /api/scholarships | Both | List scholarships |
| POST | /api/scholarships | Admin | Add scholarship |
| PUT | /api/scholarships/{id} | Admin | Edit scholarship |
| DELETE | /api/scholarships/{id} | Admin | Delete scholarship (and its applications) |
| GET | /api/students | Admin | List students |
| GET | /api/students/{id} | Both | One student / own profile |
| POST | /api/students | Admin | Add student |
| PUT | /api/students/{id} | Both | Edit student / own profile |
| DELETE | /api/students/{id} | Admin | Delete student (and their applications) |
| GET | /api/applications | Admin | All applications |
| GET | /api/applications/student/{studentId} | Student | Own applications |
| POST | /api/applications | Student | Apply |
| PUT | /api/applications/{id} | Student | Edit a pending application |
| DELETE | /api/applications/{id}?studentId= | Student | Withdraw a pending application |
| PATCH | /api/applications/{id}/status?value=APPROVED | Admin | Approve or reject |

## Rules checked by the backend
- No application after the deadline
- One application per student per scholarship
- Student's gender and category must match the scholarship
- Only pending applications can be edited or withdrawn, and only by their owner
- Passwords are stored as BCrypt hashes, never as plain text

## Deploy on Render
1. Push this repository to GitHub.
2. Create a database that Render can reach (for example a Render PostgreSQL database, or a hosted MySQL).
3. On Render: New > Web Service > connect this repository and set Root Directory to `backend`. Render detects the `Dockerfile`.
4. Add environment variables:
   - `DB_URL` - JDBC address, for PostgreSQL like `jdbc:postgresql://HOST:5432/DBNAME`
     (Render shows a `postgresql://user:pass@host/db` address: use only the host and database name here)
   - `DB_USERNAME`, `DB_PASSWORD`
   - `FRONTEND_URL` - your Vercel/Netlify address (or leave unset to allow any)
   - `ADMIN_PASSWORD` - a new admin password
5. After deploy, the API is at `https://YOUR-SERVICE.onrender.com/api`.

Free Render services sleep when unused; the first request can take about a minute.

## Known limitations
- Login returns the user but does not issue a token, so the API itself does not block a student from calling admin URLs.
  Adding Spring Security with JWT is the next step.

## Automatic check (GitHub Actions)
Every push that changes `backend/` runs `.github/workflows/backend.yml` (at the repository root): it builds the project, starts it against a PostgreSQL database
and runs `ci/api-test.sh`, which calls the main API endpoints (login, register, CRUD, apply, approve, rules).
The result shows on the repository's **Actions** tab.
