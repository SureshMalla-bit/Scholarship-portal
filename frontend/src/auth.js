// Remembers who is logged in (and their login token), using the browser's localStorage

const KEY = "scholarshipUser";

export function getUser() {
  try {
    return JSON.parse(localStorage.getItem(KEY));
  } catch {
    return null;
  }
}

// Keeps the existing token when saving updated profile details
export function saveUser(user) {
  const token = user.token || getUser()?.token;
  localStorage.setItem(KEY, JSON.stringify({ ...user, token }));
}

export function logout() {
  localStorage.removeItem(KEY);
}
