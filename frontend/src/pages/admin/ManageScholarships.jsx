import { useEffect, useState } from "react";
import api, { errorMessage } from "../../api.js";
import Message from "../../components/Message.jsx";
import ScholarshipForm from "./ScholarshipForm.jsx";
import { eligibilityText } from "../../constants.js";

// Admin CRUD for scholarships
export default function ManageScholarships() {
  const [list, setList] = useState([]);
  const [editing, setEditing] = useState(null); // null = closed, {} = new, {...} = editing
  const [message, setMessage] = useState(null);

  async function load() {
    try {
      const res = await api.get("/scholarships"); // READ
      setList(res.data);
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  useEffect(() => { load(); }, []);

  async function save(form) {
    try {
      if (form.id) await api.put("/scholarships/" + form.id, form); // UPDATE
      else await api.post("/scholarships", form);                   // CREATE
      setMessage({ ok: true, text: "Scholarship saved." });
      setEditing(null);
      load();
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  async function remove(s) {
    if (!window.confirm("Delete " + s.name + "? Its applications will also be deleted.")) return;
    try {
      await api.delete("/scholarships/" + s.id); // DELETE
      setMessage({ ok: true, text: "Scholarship deleted." });
      load();
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  return (
    <section>
      <div className="section-head">
        <h2>Scholarships ({list.length})</h2>
        {!editing && <button className="btn" onClick={() => setEditing({})}>Add scholarship</button>}
      </div>
      <Message message={message} />
      {editing && <ScholarshipForm key={editing.id || "new"} initial={editing} onSubmit={save} onCancel={() => setEditing(null)} />}
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Name</th><th>Provider</th><th>Level</th><th>Amount</th><th>Eligibility</th><th>Deadline</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {list.map((s) => (
              <tr key={s.id}>
                <td>{s.name}</td><td>{s.provider}</td><td>{s.level}</td><td>{s.amount}</td>
                <td>{eligibilityText(s)}</td><td>{s.deadline}</td>
                <td className="row-actions">
                  <button className="btn btn-small" onClick={() => setEditing(s)}>Edit</button>
                  <button className="btn btn-small btn-danger" onClick={() => remove(s)}>Delete</button>
                </td>
              </tr>
            ))}
            {list.length === 0 && <tr><td colSpan="7" className="muted">No scholarships yet.</td></tr>}
          </tbody>
        </table>
      </div>
    </section>
  );
}
