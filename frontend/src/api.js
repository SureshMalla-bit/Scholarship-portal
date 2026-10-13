import axios from "axios";
import { getUser, logout } from "./auth.js";

// One place that knows the backend address. Set VITE_API_URL in .env (or in Vercel / Netlify settings).
// When the app is served by the backend itself (one Render service), "/api" on the same address is used.
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || (import.meta.env.DEV ? "http://localhost:8080/api" : "/api")
});

// Sends the login token with every request
api.interceptors.request.use((config) => {
  const token = getUser()?.token;
  if (token) config.headers.Authorization = "Bearer " + token;
  return config;
});

// Token expired or missing: forget the user and go back to the login page
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !error.config.url.startsWith("/auth/")) {
      logout();
      window.location.assign("/login");
    }
    return Promise.reject(error);
  }
);

// Turns any error into a sentence we can show on the page
export function errorMessage(error) {
  if (error.response?.data?.message) return error.response.data.message;
  if (!error.response) return "Cannot reach the server. Check that the Spring Boot backend is running.";
  return "Something went wrong (" + error.response.status + ")";
}

export default api;
