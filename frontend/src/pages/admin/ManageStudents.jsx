import { useEffect, useState } from "react";
import api, { errorMessage } from "../../api.js";
import Message from "../../components/Message.jsx";
import StudentForm from "../../components/StudentForm.jsx";

// Admin CRUD for students
export default function ManageStudents() {
  const [list, setList] = useState([]);
  const [editing, setEditing] = useState(null);
  const [message, setMessage] = useState(null);

  async function load() {
    try {
      const res = await api.get("/students");
      setList(res.data);
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  useEffect(() => { load(); }, []);

  async function save(form) {
    try {
      if (editing.id) await api.put("/students/" + editing.id, form);
      else await api.post("/students", form);
      setMessage({ ok: true, text: "Student saved." });
      setEditing(null);
      load();
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  async function remove(s) {
    if (!window.confirm("Delete " + s.name + "? Their applications will also be deleted.")) return;
    try {
      await api.delete("/students/" + s.id);
      setMessage({ ok: true, text: "Student deleted." });
      load();
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  return (
    <section>
      <div className="section-head">
        <h2>Students ({list.length})</h2>
        {!editing && <button className="btn" onClick={() => setEditing({})}>Add student</button>}
      </div>
      <Message message={message} />
      {editing && (
        <div className="card">
          <StudentForm
            key={editing.id || "new"}
            initial={editing}
            passwordRequired={!editing.id}
            submitLabel="Save student"
            onSubmit={save}
            onCancel={() => setEditing(null)}
          />
        </div>
      )}
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Name</th><th>Email</th><th>Phone</th><th>Gender</th><th>Category</th><th>Course</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {list.map((s) => (
              <tr key={s.id}>
                <td>{s.name}</td><td>{s.email}</td><td>{s.phone}</td><td>{s.gender}</td><td>{s.category}</td><td>{s.course}</td>
                <td className="row-actions">
                  <button className="btn btn-small" onClick={() => setEditing(s)}>Edit</button>
                  <button className="btn btn-small btn-danger" onClick={() => remove(s)}>Delete</button>
                </td>
              </tr>
            ))}
            {list.length === 0 && <tr><td colSpan="7" className="muted">No students yet.</td></tr>}
          </tbody>
        </table>
      </div>
    </section>
  );
}
