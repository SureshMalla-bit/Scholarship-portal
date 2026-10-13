import { useEffect, useState } from "react";
import api, { errorMessage } from "../../api.js";
import { getUser } from "../../auth.js";
import Message from "../../components/Message.jsx";
import ApplicationForm from "./ApplicationForm.jsx";

// Student READS, UPDATES and DELETES (withdraws) their own applications
export default function MyApplications() {
  const user = getUser();
  const [list, setList] = useState([]);
  const [editing, setEditing] = useState(null);
  const [message, setMessage] = useState(null);

  async function load() {
    try {
      const res = await api.get("/applications/student/" + user.id);
      setList(res.data);
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  useEffect(() => { load(); }, []);

  async function update(form) {
    try {
      await api.put("/applications/" + editing.id, {
        studentId: user.id,
        scholarshipId: editing.scholarship.id,
        course: form.course,
        percentage: form.percentage,
        annualIncome: form.annualIncome
      });
      setMessage({ ok: true, text: "Application updated." });
      setEditing(null);
      load();
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  async function withdraw(app) {
    if (!window.confirm("Withdraw your application for " + app.scholarship.name + "?")) return;
    try {
      await api.delete(`/applications/${app.id}?studentId=${user.id}`);
      setMessage({ ok: true, text: "Application withdrawn." });
      load();
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  return (
    <section>
      <h2>My applications ({list.length})</h2>
      <Message message={message} />
      {editing && (
        <ApplicationForm
          key={editing.id}
          title={"Edit: " + editing.scholarship.name}
          initial={{ course: editing.course, percentage: editing.percentage, annualIncome: editing.annualIncome }}
          submitLabel="Save changes"
          onSubmit={update}
          onCancel={() => setEditing(null)}
        />
      )}
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Scholarship</th><th>Amount</th><th>Course</th><th>%</th><th>Applied</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {list.map((a) => (
              <tr key={a.id}>
                <td>{a.scholarship.name}</td><td>{a.scholarship.amount}</td><td>{a.course}</td><td>{a.percentage}</td>
                <td>{a.appliedOn}</td><td><span className={"badge " + a.status}>{a.status}</span></td>
                <td className="row-actions">
                  {a.status === "PENDING" ? (
                    <>
                      <button className="btn btn-small" onClick={() => setEditing(a)}>Edit</button>
                      <button className="btn btn-small btn-danger" onClick={() => withdraw(a)}>Withdraw</button>
                    </>
                  ) : <span className="muted">Decision made</span>}
                </td>
              </tr>
            ))}
            {list.length === 0 && <tr><td colSpan="7" className="muted">You have not applied yet. Go to Scholarships to apply.</td></tr>}
          </tbody>
        </table>
      </div>
    </section>
  );
}
