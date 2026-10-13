// Green box for success, red box for errors
export default function Message({ message }) {
  if (!message?.text) return null;
  return <p className={"message " + (message.ok ? "ok" : "error")}>{message.text}</p>;
}
