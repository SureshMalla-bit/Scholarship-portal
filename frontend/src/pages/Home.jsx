import { Link } from "react-router-dom";
import Navbar from "../components/Navbar.jsx";

export default function Home() {
  return (
    <>
      <Navbar />
      <main className="page home">
        <h1>Find and apply for scholarships in one place</h1>
        <p>
          Students can browse scholarships, check if they are eligible, apply online and track the status.
          The admin manages scholarships, students and applications.
        </p>
        <div className="actions">
          <Link className="btn" to="/login">Log in</Link>
          <Link className="btn btn-light" to="/register">Register as a student</Link>
        </div>
      </main>
    </>
  );
}
