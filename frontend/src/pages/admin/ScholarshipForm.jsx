import { useState } from "react";
import { CATEGORIES, LEVELS } from "../../constants.js";

const EMPTY = { name: "", provider: "", level: "UG", amount: "", deadline: "", gender: "All", categories: "All", description: "" };

// Add / edit scholarship form for the admin
export default function ScholarshipForm({ initial, onSubmit, onCancel }) {
  const [form, setForm] = useState({ ...EMPTY, ...initial });
  const chosen = form.categories === "All" ? [] : form.categories.split(",");

  function change(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  // Category checkboxes: none ticked means "All"
  function toggleCategory(category) {
    const next = chosen.includes(category) ? chosen.filter((c) => c !== category) : [...chosen, category];
    setForm({ ...form, categories: next.length ? next.join(",") : "All" });
  }

  function submit(e) {
    e.preventDefault();
    onSubmit(form);
  }

  return (
    <form className="card form-grid" onSubmit={submit}>
      <label className="full">Scholarship name<input name="name" value={form.name} onChange={change} required /></label>
      <label>Provider<input name="provider" value={form.provider} onChange={change} required /></label>
      <label>Amount<input name="amount" placeholder="Rs 50,000 per year" value={form.amount} onChange={change} required /></label>
      <label>Level
        <select name="level" value={form.level} onChange={change}>{LEVELS.map((l) => <option key={l}>{l}</option>)}</select>
      </label>
      <label>Deadline<input name="deadline" type="date" value={form.deadline} onChange={change} required /></label>
      <label>Open to gender
        <select name="gender" value={form.gender} onChange={change}>
          <option value="All">All</option><option value="Female">Female only</option><option value="Male">Male only</option>
        </select>
      </label>
      <fieldset className="full">
        <legend>Open to categories (none ticked = all categories)</legend>
        {CATEGORIES.map((c) => (
          <label key={c} className="check">
            <input type="checkbox" checked={chosen.includes(c)} onChange={() => toggleCategory(c)} /> {c}
          </label>
        ))}
      </fieldset>
      <label className="full">Description<textarea name="description" rows="3" value={form.description || ""} onChange={change} /></label>
      <div className="full actions">
        <button className="btn" type="submit">Save scholarship</button>
        <button className="btn btn-light" type="button" onClick={onCancel}>Cancel</button>
      </div>
    </form>
  );
}
