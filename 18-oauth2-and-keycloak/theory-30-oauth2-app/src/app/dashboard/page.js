"use client";
import LogoutBtn from "@/components/auth/LogoutBtn";
import Link from "next/link";

export default function Dashboard() {
  return (
    <div>
      <h1>Welcome to Dashboard (Protected)</h1>
      <Link href="/customers">
        <button style={{ marginTop: "16px", marginRight: "5px" }}>
          View Customer List
        </button>
      </Link>
      <Link href="/about-me">
        <button style={{ marginTop: "16px", marginRight: "5px" }}>
          View User Profile
        </button>
      </Link>
      <LogoutBtn />
    </div>
  );
}
