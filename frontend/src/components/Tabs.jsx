// Row of tab buttons used by both dashboards
export default function Tabs({ tabs, active, onChange }) {
  return (
    <nav className="tabs">
      {tabs.map((tab) => (
        <button
          key={tab.id}
          className={"tab" + (tab.id === active ? " active" : "")}
          onClick={() => onChange(tab.id)}
        >
          {tab.label}
        </button>
      ))}
    </nav>
  );
}
