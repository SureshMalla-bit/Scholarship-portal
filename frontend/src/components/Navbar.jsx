import { useNavigate } from "react-router-dom";
import { getUser, logout } from "../auth.js";

export default function Navbar() {
  const navigate = useNavigate();
  const user = getUser();

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <header className="navbar">
      <div className="brand">Scholarship Management System</div>
      {user && (
        <div className="nav-user">
          <span>{user.name} ({user.role === "ADMIN" ? "Admin" : "Student"})</span>
          <button className="btn btn-light" onClick={handleLogout}>Log out</button>
        </div>
      )}
    </header>
  );
}
