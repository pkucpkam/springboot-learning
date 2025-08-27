// /app/(protected)/logout/route.js
import { NextResponse } from "next/server";

const ISSUER = process.env.NEXT_PUBLIC_KEYCLOAK_ISSUER; // eg: http://localhost:8080/realms/master
const CLIENT_ID = process.env.NEXT_PUBLIC_KEYCLOAK_CLIENT_ID; // eg: keycloak-idp

function buildLogoutUrl({ issuer, clientId, redirectUri, idToken }) {
  if (!issuer || !clientId || !redirectUri) {
    throw new Error("Missing env: ISSUER / CLIENT_ID / redirectUri");
  }
  const base = issuer.replace(/\/$/, "");
  const url = new URL(`${base}/protocol/openid-connect/logout`);
  url.searchParams.set("client_id", clientId);
  url.searchParams.set("post_logout_redirect_uri", redirectUri);
  if (idToken) url.searchParams.set("id_token_hint", idToken);
  return url.toString();
}

function clearNextAuthCookies(req, res) {
  const namesToClear = req.cookies
    .getAll()
    .map((c) => c.name)
    .filter(
      (name) =>
        // session token (often be chunked .0/.1)
        name === "next-auth.session-token" ||
        name.startsWith("next-auth.session-token.") ||
        name === "__Secure-next-auth.session-token" ||
        name.startsWith("__Secure-next-auth.session-token.") ||
        name === "next-auth.csrf-token" ||
        name === "next-auth.callback-url" ||
        name === "access-token" ||
        name === "id-token" ||
        name === "app-roles"
    );

  for (const n of namesToClear) {
    res.cookies.set(n, "", { path: "/", expires: new Date(0) });
  }
}

export async function POST(req) {
  try {
    const body = await req.json().catch(() => ({}));
    const origin =
      process.env.NEXT_PUBLIC_FE_URL ||
      process.env.NEXT_PUBLIC_BASE_URL ||
      process.env.NEXTAUTH_URL ||
      new URL(req.url).origin; // fallback will not be undefined

    const logoutUrl = buildLogoutUrl({
      issuer: ISSUER,
      clientId: CLIENT_ID,
      redirectUri: `${origin}/login`,
      idToken: body?.idToken,
    });

    const res = NextResponse.redirect(logoutUrl, { status: 302 });
    clearNextAuthCookies(req, res); // clear all cookies of session NextAuth (.0/.1, __Secure-...)
    return res;
  } catch (e) {
    return NextResponse.json(
      { ok: false, error: String(e?.message ?? e) },
      { status: 500 }
    );
  }
}

// support GET /logout if you use <Link href="/logout">
export const GET = POST;
