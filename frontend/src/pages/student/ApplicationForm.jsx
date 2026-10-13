import { useState } from "react";

// Used to apply for a scholarship and to edit a pending application
export default function ApplicationForm({ title, initial, submitLabel, onSubmit, onCancel }) {
  const [form, setForm] = useState({ course: "", percentage: "", annualIncome: "", ...initial });

  function change(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  function submit(e) {
    e.preventDefault();
    onSubmit({ ...form, percentage: Number(form.percentage), annualIncome: Number(form.annualIncome) });
  }

  return (
    <form className="card form-grid" onSubmit={submit}>
      <h3 className="full">{title}</h3>
      <label className="full">Course<input name="course" value={form.course} onChange={change} required /></label>
      <label>Percentage / CGPA as %<input name="percentage" type="number" min="0" max="100" step="0.01" value={form.percentage} onChange={change} required /></label>
      <label>Annual family income (Rs)<input name="annualIncome" type="number" min="0" value={form.annualIncome} onChange={change} required /></label>
      <div className="full actions">
        <button className="btn" type="submit">{submitLabel}</button>
        <button className="btn btn-light" type="button" onClick={onCancel}>Cancel</button>
      </div>
    </form>
  );
}
