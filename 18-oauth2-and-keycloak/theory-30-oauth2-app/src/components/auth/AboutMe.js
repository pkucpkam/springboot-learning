"use client";
import { useEffect, useState } from "react";

export default function AboutMe() {
  const [me, setMe] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  useEffect(() => {
    const controller = new AbortController();
    (async () => {
      try {
        const res = await fetch("/api/me", {
          method: "GET",
          credentials: "same-origin",
          headers: { Accept: "application/json" },
          signal: controller.signal,
          cache: "no-store",
        });
        if (!res.ok)
          throw new Error(
            res.status === 401
              ? "Bạn chưa đăng nhập (401)."
              : `Lỗi gọi /api/me: ${res.status}`
          );
        const data = await res.json();
        setMe(data);
      } catch (e) {
        if (e.name !== "AbortError") setErr(e.message || "Đã có lỗi xảy ra");
      } finally {
        setLoading(false);
      }
    })();
    return () => controller.abort();
  }, []);

  if (loading) return <div>Đang tải thông tin…</div>;
  if (err) return <div style={{ color: "red" }}>Error: {err}</div>;

  const username = me?.preferred_username ?? me?.name ?? me?.username ?? "-";
  const roles =
    Array.isArray(me?.roles) && me.roles.length > 0 ? me.roles.join(", ") : "—"; // will be "—" because default userinfo will not have roles

  return (
    <div>
      <h2>About Me</h2>
      <div>
        <b>Username:</b> {username}
      </div>
      <div>
        <b>Email:</b> {me?.email ?? "-"}
      </div>
      <div>
        <b>Sub:</b> {me?.sub ?? "-"}
      </div>
      <div>
        <b>Roles:</b> {roles}
      </div>
    </div>
  );
}
