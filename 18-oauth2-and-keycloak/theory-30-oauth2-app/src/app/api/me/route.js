import { getToken } from "next-auth/jwt";

export const dynamic = "force-dynamic";
const BE = process.env.NEXT_PUBLIC_BE_URL ?? "http://localhost:8081";
const SECRET = process.env.NEXTAUTH_SECRET;

export async function GET(req) {
  // 1) Call session to TRIGGER jwt-callback (if access token is close to/expired, it will refresh)
  const origin = new URL(req.url).origin;
  await fetch(`${origin}/api/auth/session`, { cache: "no-store" });

  // 2) Get latest token after refresh
  const token = await getToken({ req, secret: SECRET });
  if (!token?.accessToken) {
    return Response.json({ error: "Unauthorized" }, { status: 401 });
  }

  // 3) Call BE with access token (refreshed if needed)
  const r = await fetch(`${BE}/api/auth/me`, {
    headers: {
      Authorization: `Bearer ${token.accessToken}`,
      Accept: "application/json",
    },
    cache: "no-store",
  });

  if (!r.ok) {
    const text = await r.text().catch(() => "");
    return Response.json({ error: text || r.statusText }, { status: r.status });
  }
  const data = await r.json();
  return Response.json(data);
}
