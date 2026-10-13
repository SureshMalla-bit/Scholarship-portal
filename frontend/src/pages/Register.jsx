import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { errorMessage } from "../api.js";
import Navbar from "../components/Navbar.jsx";
import Message from "../components/Message.jsx";
import StudentForm from "../components/StudentForm.jsx";

export default function Register() {
  const navigate = useNavigate();
  const [message, setMessage] = useState(null);

  async function register(form) {
    try {
      await api.post("/auth/register", form);
      navigate("/login");
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  return (
    <>
      <Navbar />
      <main className="page narrow">
        <h1>Student registration</h1>
        <Message message={message} />
        <div className="card">
          <StudentForm passwordRequired submitLabel="Create account" onSubmit={register} />
          <p className="muted">Already registered? <Link to="/login">Log in</Link></p>
        </div>
      </main>
    </>
  );
}
