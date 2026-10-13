import { useEffect, useState } from "react";
import api, { errorMessage } from "../../api.js";
import Message from "../../components/Message.jsx";

// Admin views every application and approves or rejects it
export default function ManageApplications() {
  const [list, setList] = useState([]);
  const [filter, setFilter] = useState("");
  const [message, setMessage] = useState(null);

  async function load() {
    try {
      const res = await api.get("/applications");
      setList(res.data);
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  useEffect(() => { load(); }, []);

  async function changeStatus(app, value) {
    try {
      await api.patch(`/applications/${app.id}/status?value=${value}`);
      setMessage({ ok: true, text: "Status changed to " + value + "." });
      load();
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  const shown = filter ? list.filter((a) => a.status === filter) : list;

  return (
    <section>
      <div className="section-head">
        <h2>Applications ({shown.length})</h2>
        <select value={filter} onChange={(e) => setFilter(e.target.value)} aria-label="Filter by status">
          <option value="">All statuses</option><option>PENDING</option><option>APPROVED</option><option>REJECTED</option>
        </select>
      </div>
      <Message message={message} />
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Student</th><th>Scholarship</th><th>Course</th><th>%</th><th>Family income</th><th>Applied</th><th>Status</th><th>Decision</th></tr>
          </thead>
          <tbody>
            {shown.map((a) => (
              <tr key={a.id}>
                <td>{a.student.name}<br /><span className="muted">{a.student.gender} · {a.student.category}</span></td>
                <td>{a.scholarship.name}</td><td>{a.course}</td><td>{a.percentage}</td>
                <td>Rs {Number(a.annualIncome).toLocaleString("en-IN")}</td><td>{a.appliedOn}</td>
                <td><span className={"badge " + a.status}>{a.status}</span></td>
                <td className="row-actions">
                  <button className="btn btn-small btn-ok" disabled={a.status === "APPROVED"} onClick={() => changeStatus(a, "APPROVED")}>Approve</button>
                  <button className="btn btn-small btn-danger" disabled={a.status === "REJECTED"} onClick={() => changeStatus(a, "REJECTED")}>Reject</button>
                </td>
              </tr>
            ))}
            {shown.length === 0 && <tr><td colSpan="8" className="muted">No applications.</td></tr>}
          </tbody>
        </table>
      </div>
    </section>
  );
}
