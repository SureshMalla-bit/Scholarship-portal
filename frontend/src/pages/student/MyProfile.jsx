import { useEffect, useState } from "react";
import api, { errorMessage } from "../../api.js";
import { getUser, saveUser } from "../../auth.js";
import Message from "../../components/Message.jsx";
import StudentForm from "../../components/StudentForm.jsx";

// Student READS and UPDATES their own details
export default function MyProfile() {
  const user = getUser();
  const [profile, setProfile] = useState(null);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    api.get("/students/" + user.id)
      .then((res) => setProfile(res.data))
      .catch((err) => setMessage({ ok: false, text: errorMessage(err) }));
  }, []);

  async function save(form) {
    try {
      const res = await api.put("/students/" + user.id, form);
      saveUser(res.data); // keep the stored login details up to date
      setProfile(res.data);
      setMessage({ ok: true, text: "Profile updated." });
    } catch (err) {
      setMessage({ ok: false, text: errorMessage(err) });
    }
  }

  return (
    <section>
      <h2>My profile</h2>
      <Message message={message} />
      {profile && (
        <div className="card">
          <StudentForm key={profile.email} initial={profile} passwordRequired={false} submitLabel="Save profile" onSubmit={save} />
        </div>
      )}
    </section>
  );
}
