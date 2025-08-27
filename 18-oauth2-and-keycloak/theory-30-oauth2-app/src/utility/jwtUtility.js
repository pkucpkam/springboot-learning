import { cookies } from "next/headers";

export async function pickAccessToken(req) {
  // 1) Priority to get token from Cookie (HttpOnly)
  const jar = await cookies();
  const fromCookie = jar.get("access-token")?.value;
  if (fromCookie) return fromCookie;

  // 2) Fallback: get token from JWT of NextAuth
  const token = await getToken({ req, secret: SECRET }).catch(() => null);
  if (token?.accessToken) return token.accessToken;

  return null;
}

export function decodePayload(jwt) {
  if (!jwt) return undefined;
  try {
    const [, payload] = jwt.split(".");
    const norm = payload
      .replace(/-/g, "+")
      .replace(/_/g, "/")
      .padEnd(Math.ceil(payload.length / 4) * 4, "=");
    return JSON.parse(Buffer.from(norm, "base64").toString("utf8"));
  } catch {
    return undefined;
  }
}

export function extractRolesFromAT(accessToken) {
  const p = decodePayload(accessToken);
  const realm = Array.isArray(p?.realm_access?.roles)
    ? p.realm_access.roles
    : [];
  const ra =
    p?.resource_access && typeof p.resource_access === "object"
      ? p.resource_access
      : {};
  const clientRoles = Object.values(ra).flatMap((v) =>
    Array.isArray(v?.roles) ? v.roles : []
  );
  return Array.from(new Set([...realm, ...clientRoles]));
}

export async function setHttpOnlyCookie(name, value, maxAgeSec) {
  const jar = await cookies();
  jar.set(name, value, {
    httpOnly: true,
    sameSite: "lax",
    secure: process.env.NODE_ENV === "production",
    path: "/",
    maxAge: Math.max(1, maxAgeSec),
  });
}
