import { useState } from "react";
import { CATEGORIES, GENDERS } from "../constants.js";

const EMPTY = { name: "", email: "", password: "", gender: "", category: "", phone: "", course: "" };

// Used by Register, by the admin (add / edit student) and by the student's profile page.
// passwordRequired = true when creating a new account.
export default function StudentForm({ initial, passwordRequired, submitLabel, onSubmit, onCancel }) {
  const [form, setForm] = useState({ ...EMPTY, ...initial, password: "" });

  function change(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  function submit(e) {
    e.preventDefault();
    onSubmit(form);
  }

  return (
    <form className="form-grid" onSubmit={submit}>
      <label>Full name<input name="name" value={form.name} onChange={change} required /></label>
      <label>Email<input name="email" type="email" value={form.email} onChange={change} required /></label>
      <label>
        {passwordRequired ? "Password" : "New password (leave empty to keep)"}
        <input name="password" type="password" minLength={6} value={form.password} onChange={change} required={passwordRequired} />
      </label>
      <label>Phone<input name="phone" value={form.phone || ""} onChange={change} /></label>
      <label>Gender
        <select name="gender" value={form.gender || ""} onChange={change} required>
          <option value="">Select</option>
          {GENDERS.map((g) => <option key={g}>{g}</option>)}
        </select>
      </label>
      <label>Category
        <select name="category" value={form.category || ""} onChange={change} required>
          <option value="">Select</option>
          {CATEGORIES.map((c) => <option key={c}>{c}</option>)}
        </select>
      </label>
      <label className="full">Course<input name="course" placeholder="B.Tech ECE" value={form.course || ""} onChange={change} /></label>
      <div className="full actions">
        <button className="btn" type="submit">{submitLabel}</button>
        {onCancel && <button className="btn btn-light" type="button" onClick={onCancel}>Cancel</button>}
      </div>
    </form>
  );
}
