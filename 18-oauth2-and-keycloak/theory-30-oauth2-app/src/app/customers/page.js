// src/app/customers/page.js (server component)
import CustomerList from "@/components/main/customer/CustomerList";
import { cookies, headers } from "next/headers";

async function getCustomers() {
  const jar = await cookies();
  const h = await headers();

  const proto = h.get("x-forwarded-proto") ?? "http";
  const host = h.get("host") ?? "localhost:3000";
  const base = `${proto}://${host}`;

  const res = await fetch(`${base}/api/bff/customers`, {
    headers: { cookie: jar.toString() }, // forward cookie phiên
    cache: "no-store",
  });
  if (res.status === 401 || res.status === 403) {
    const err = new Error("UNAUTHORIZED");
    err.status = res.status;
    throw err;
  }
  return res.json();
}

export default async function Page() {
  const data = await getCustomers();
  return <CustomerList customers={data} />;
}
