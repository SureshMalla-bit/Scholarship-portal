import { useEffect, useState } from "react";
import api, { errorMessage } from "../../api.js";
import { getUser } from "../../auth.js";
import Message from "../../components/Message.jsx";
import ApplicationForm from "./ApplicationForm.jsx";
import { eligibilityText, isClosed, LEVELS } from "../../constants.js";

// Student sees scholarships (READ) and applies (CREATE)
export default function BrowseScholarships({ onApplied }) {
  const user = getUser();
  const [list, setList] = useState([]);
  const [search, setSearch] = useState("");
  const [level, setLevel] = useState("");
  const [onlyEligible, setOnlyEligible] = useState(false);
  const [applying, setApplying] = useState(null);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    api.get("/scholarships")
      .then((res) => setList(res.data))
      .catch((err) => setMessage({ ok: false, text: errorMessage(err) }));
  }, []);

  // Same rules as the backend, used here only to filter the list
  function eligible(s) {
    const genderOk = s.gender === "All" || s.gender === user.gender;
    const categoryOk = s.categories === "All" || s.categories.split(",").includes(user.category);
    return genderOk && categoryOk;
  }

  const shown = list.filter((s) =>
    (!level || s.level === level) &&
    (!onlyEligible || eligible(s)) &&
    (s.name + " " + s.provider).toLowerCase().includes(search.toLowerCase())
  );

  async function apply(form) {
    try {
      await api.post("/applications", { ...form, studentId: user.id, scholarshipId: applying.id });
      setApplying(null);
      onApplied();
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  return (
    <section>
      <div className="toolbar">
        <input placeholder="Search by name or provider" value={search} onChange={(e) => setSearch(e.target.value)} />
        <select value={level} onChange={(e) => setLevel(e.target.value)} aria-label="Level">
          <option value="">All levels</option>
          {LEVELS.map((l) => <option key={l}>{l}</option>)}
        </select>
        <label className="check">
          <input type="checkbox" checked={onlyEligible} onChange={(e) => setOnlyEligible(e.target.checked)} /> Only ones I can apply for
        </label>
      </div>
      <Message message={message} />
      {applying && (
        <ApplicationForm
          title={"Apply: " + applying.name}
          initial={{ course: user.course || "" }}
          submitLabel="Submit application"
          onSubmit={apply}
          onCancel={() => setApplying(null)}
        />
      )}
      <div className="cards">
        {shown.map((s) => (
          <article key={s.id} className="card scholarship">
            <h3>{s.name}</h3>
            <p className="muted">{s.provider} · {s.level}</p>
            <p className="amount">{s.amount}</p>
            <p>{s.description}</p>
            <p className="muted">Open to: {eligibilityText(s)}</p>
            <p className="muted">Deadline: {s.deadline}{isClosed(s) ? " (closed)" : ""}</p>
            <button className="btn" disabled={isClosed(s) || !eligible(s)} onClick={() => { setMessage(null); setApplying(s); }}>
              {isClosed(s) ? "Closed" : eligible(s) ? "Apply" : "Not eligible"}
            </button>
          </article>
        ))}
        {shown.length === 0 && <p className="muted">No scholarships match.</p>}
      </div>
    </section>
  );
}
