import { Navigate } from "react-router-dom";
import { getUser } from "../auth.js";

// Lets a page open only for a logged-in user with the right role
export default function ProtectedRoute({ role, children }) {
  const user = getUser();
  if (!user) return <Navigate to="/login" replace />;
  if (user.role !== role) return <Navigate to={user.role === "ADMIN" ? "/admin" : "/student"} replace />;
  return children;
}
