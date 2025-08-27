// src/app/api/bff/[...path]/route.js
import { getToken } from "next-auth/jwt";

const BE = process.env.NEXT_PUBLIC_BE_URL; // e.g. http://localhost:8081
export const dynamic = "force-dynamic"; // prevent cache auth

export async function GET(req, { params }) {
    const { path } = await params;
  // read JWT token from cookie (next-auth store at cookie)
  const token = await getToken({ req, secret: process.env.NEXTAUTH_SECRET });

  if (!token?.accessToken) {
    return Response.json({ error: "Unauthorized" }, { status: 401 });
  }

  // map path: /api/bff/customers -> http://BE/api/customers
  const target = `${BE}/api/${path.join("/")}`;

  const r = await fetch(target, {
    headers: {
      Authorization: `Bearer ${token.accessToken}`,
    },
    cache: "no-store",
  });

  const text = await r.text(); // read text to prevent crash if not JSON
  return new Response(text, {
    status: r.status,
    headers: {
      "content-type": r.headers.get("content-type") ?? "application/json",
    },
  });
}
