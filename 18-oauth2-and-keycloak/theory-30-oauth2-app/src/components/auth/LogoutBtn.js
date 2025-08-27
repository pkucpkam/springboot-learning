// /components/auth/logoutBtn.js
export default function LogoutBtn() {
  return (
    <form action="/logout" method="post" style={{ marginTop: "16px" }}>
      <button type="submit">Logout</button>
    </form>
  );
}
