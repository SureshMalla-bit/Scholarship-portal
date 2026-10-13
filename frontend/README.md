# Scholarship Management System - Frontend

React (Vite) + CSS. Talks to the Spring Boot backend in `../backend`.

## Run on your laptop
1. Install Node.js 18 or newer.
2. Start the backend first (it runs on http://localhost:8080).
3. In this folder:

       npm install
       npm run dev

4. Open http://localhost:5173

The backend address is read from `VITE_API_URL`. Copy `.env.example` to `.env` to change it.
If it is not set, http://localhost:8080/api is used by `npm run dev`, and `/api` on the same address in a production build.

## Logins
- Admin: `admin@scholarship.com` / `admin123` (created by the backend)
- Students: register on the Register page

## Pages
| Page | File | What it does |
|---|---|---|
| Home | `pages/Home.jsx` | Links to Login and Register |
| Login | `pages/Login.jsx` | Logs in and opens the right dashboard by role |
| Register | `pages/Register.jsx` | New student account |
| Admin dashboard | `pages/admin/` | CRUD on scholarships and students, approve or reject applications |
| Student dashboard | `pages/student/` | Browse and apply, view/edit/withdraw own applications, edit own profile |

Other files: `api.js` (Axios setup), `auth.js` (remembers the logged-in user), `constants.js` (lists and helpers),
`components/` (Navbar, Tabs, Message, StudentForm, ProtectedRoute), `App.css` (all styles).

## CRUD covered
| | Create | Read | Update | Delete |
|---|---|---|---|---|
| Admin | scholarship, student | scholarships, students, applications | scholarship, student, application status | scholarship, student |
| Student | account, application | scholarships, own applications, profile | own profile, pending application | withdraw pending application |

## Deploy
By default the website is deployed together with the API as one Render service (see `render.yaml` and the main README),
and it then calls `/api` on its own address. To host it separately on Vercel or Netlify instead:

1. Push this repository to GitHub.
2. Import the repository in Vercel or Netlify, set Root Directory to `frontend`. Build command `npm run build`, output folder `dist`.
3. Add the environment variable `VITE_API_URL` = `https://YOUR-BACKEND.onrender.com/api`.
4. Deploy. `vercel.json` and `public/_redirects` make page refreshes work on both platforms.
