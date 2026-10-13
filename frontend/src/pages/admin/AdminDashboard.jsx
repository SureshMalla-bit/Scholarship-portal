import { useState } from "react";
import Navbar from "../../components/Navbar.jsx";
import Tabs from "../../components/Tabs.jsx";
import ManageScholarships from "./ManageScholarships.jsx";
import ManageStudents from "./ManageStudents.jsx";
import ManageApplications from "./ManageApplications.jsx";

const TABS = [
  { id: "scholarships", label: "Scholarships" },
  { id: "students", label: "Students" },
  { id: "applications", label: "Applications" }
];

export default function AdminDashboard() {
  const [tab, setTab] = useState("scholarships");
  return (
    <>
      <Navbar />
      <main className="page">
        <h1>Admin dashboard</h1>
        <Tabs tabs={TABS} active={tab} onChange={setTab} />
        {tab === "scholarships" && <ManageScholarships />}
        {tab === "students" && <ManageStudents />}
        {tab === "applications" && <ManageApplications />}
      </main>
    </>
  );
}
