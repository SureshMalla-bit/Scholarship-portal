import { useState } from "react";
import Navbar from "../../components/Navbar.jsx";
import Tabs from "../../components/Tabs.jsx";
import BrowseScholarships from "./BrowseScholarships.jsx";
import MyApplications from "./MyApplications.jsx";
import MyProfile from "./MyProfile.jsx";
import { getUser } from "../../auth.js";

const TABS = [
  { id: "browse", label: "Scholarships" },
  { id: "applications", label: "My applications" },
  { id: "profile", label: "My profile" }
];

export default function StudentDashboard() {
  const [tab, setTab] = useState("browse");
  const user = getUser();
  return (
    <>
      <Navbar />
      <main className="page">
        <h1>Welcome, {user.name}</h1>
        <Tabs tabs={TABS} active={tab} onChange={setTab} />
        {tab === "browse" && <BrowseScholarships onApplied={() => setTab("applications")} />}
        {tab === "applications" && <MyApplications />}
        {tab === "profile" && <MyProfile />}
      </main>
    </>
  );
}
