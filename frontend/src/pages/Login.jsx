import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { errorMessage } from "../api.js";
import { saveUser } from "../auth.js";
import Navbar from "../components/Navbar.jsx";
import Message from "../components/Message.jsx";

export default function Login() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [message, setMessage] = useState(null);

  async function submit(e) {
    e.preventDefault();
    try {
      const res = await api.post("/auth/login", form);
      saveUser(res.data);
      // Send the user to the dashboard for their role
      navigate(res.data.role === "ADMIN" ? "/admin" : "/student");
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  return (
    <>
      <Navbar />
      <main className="page narrow">
        <h1>Log in</h1>
        <Message message={message} />
        <form className="card form-stack" onSubmit={submit}>
          <label>Email<input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required /></label>
          <label>Password<input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required /></label>
          <button className="btn" type="submit">Log in</button>
          <p className="muted">New student? <Link to="/register">Create an account</Link></p>
        </form>
      </main>
    </>
  );
}
