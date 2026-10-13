import axios from "axios";

// One place that knows the backend address. Set VITE_API_URL in .env (or in Vercel / Netlify settings).
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api"
});

// Turns any error into a sentence we can show on the page
export function errorMessage(error) {
  if (error.response?.data?.message) return error.response.data.message;
  if (!error.response) return "Cannot reach the server. Check that the Spring Boot backend is running.";
  return "Something went wrong (" + error.response.status + ")";
}

export default api;
