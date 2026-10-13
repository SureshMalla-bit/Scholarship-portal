# Scholarship Management System - Backend

Spring Boot REST API with MySQL. The React frontend is in `../frontend`.

## Tech
Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Bean Validation, BCrypt (spring-security-crypto), JWT login tokens, MySQL / PostgreSQL.

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
    config/       CORS, BCrypt, sample data, serving the React build
    security/     Login tokens (JWT) and the checks for who may call each URL

## REST API
Login returns a `token`. Send it as `Authorization: Bearer <token>` on every other call.
Without a token only login, register and reading scholarships work (401 otherwise); a student calling an admin URL,
or another student's data, gets 403.

| Method | URL | Who | Purpose |
|---|---|---|---|
| POST | /api/auth/register | Student | Create account |
| POST | /api/auth/login | Both | Log in, returns user with role and token |
| GET | /api/scholarships | Anyone | List scholarships |
| POST | /api/scholarships | Admin | Add scholarship |
| PUT | /api/scholarships/{id} | Admin | Edit scholarship |
| DELETE | /api/scholarships/{id} | Admin | Delete scholarship (and its applications) |
| GET | /api/students | Admin | List students |
| GET | /api/students/{id} | Admin, or that student | One student / own profile |
| POST | /api/students | Admin | Add student |
| PUT | /api/students/{id} | Admin, or that student | Edit student / own profile |
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
- Every call is checked against the login token: role and ownership come from the token, not from the request

## Deploy
The easiest way is the Render Blueprint at the repository root (see the main README): it deploys this API together with
the React app as one service. To deploy only the API, create a Render web service with Root Directory `backend`
(it uses `backend/Dockerfile`) and set these environment variables:
- `DB_URL` - JDBC address, like `jdbc:postgresql://HOST:5432/DBNAME` (or `DB_HOST`, `DB_PORT`, `DB_NAME` separately)
- `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET` - long random text used to sign login tokens
- `ADMIN_PASSWORD` - the admin password
- `FRONTEND_URL` - the website's address, if it is hosted somewhere else

## Automatic check (GitHub Actions)
Every push that changes `backend/` runs `.github/workflows/backend.yml` (at the repository root): it builds the project, starts it against a PostgreSQL database
and runs `ci/api-test.sh`, which calls the main API endpoints (login, register, CRUD, apply, approve, rules, and the token checks).
The result shows on the repository's **Actions** tab.
