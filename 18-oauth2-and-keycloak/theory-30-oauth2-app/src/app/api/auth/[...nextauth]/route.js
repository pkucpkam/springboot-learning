// app/api/auth/[...nextauth]/route.ts
import { prisma } from "@/lib/prisma";
import { extractRolesFromAT, setHttpOnlyCookie } from "@/utility/jwtUtility";
import { PrismaAdapter } from "@auth/prisma-adapter";
import NextAuth from "next-auth";
import Keycloak from "next-auth/providers/keycloak";

export const runtime = "nodejs"; // need for Prisma with App Router

const ISSUER = process.env.NEXT_PUBLIC_KEYCLOAK_ISSUER || ""; // http://localhost:8080/realms/master
const CLIENT_ID = process.env.NEXT_PUBLIC_KEYCLOAK_CLIENT_ID || "";
const CLIENT_SECRET = process.env.NEXT_PUBLIC_KEYCLOAK_CLIENT_SECRET || "";

// --- Refresh token with Keycloak (right payload) ---
async function refreshWithKeycloak(refreshToken) {
  const body = new URLSearchParams();
  body.set("grant_type", "refresh_token");
  body.set("client_id", CLIENT_ID);
  if (CLIENT_SECRET) body.set("client_secret", CLIENT_SECRET);
  body.set("refresh_token", String(refreshToken));

  const r = await fetch(`${ISSUER}/protocol/openid-connect/token`, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body,
    cache: "no-store",
  });

  const raw = await r.text();
  if (!r.ok) {
    let code;
    try {
      const j = JSON.parse(raw);
      const msg = (j.error_description || j.error || "").toLowerCase();
      if (
        j.error === "invalid_grant" ||
        msg.includes("refresh") ||
        msg.includes("not active")
      )
        code = "REFRESH_TOKEN_EXPIRED";
      if (j.error === "invalid_client") code = "INVALID_CLIENT";
    } catch {}
    const err = new Error(`KC refresh failed ${r.status}`);
    if (code) err.code = code;
    throw err;
  }

  return JSON.parse(raw);
}

// --- Original Adapter ---
const base = PrismaAdapter(prisma);

// --- Adapter wrapped to "sanitize" linkAccount data ---
const adapter = {
  ...base,
  async linkAccount(account) {
    // Only map the valid columns in Account table in schema Prisma
    // If your schema has 'session_state' then keep it; if not — remove that line.
    const allowed = {
      userId: account.userId,
      type: account.type,
      provider: account.provider,
      providerAccountId: account.providerAccountId,
      refresh_token: account.refresh_token ?? null,
      access_token: account.access_token ?? null,
      // Prefer expires_at if provider has set it; otherwise convert from expires_in
      expires_at:
        typeof account.expires_at === "number"
          ? account.expires_at
          : account.expires_in
          ? Math.floor(Date.now() / 1000) + Number(account.expires_in)
          : null,
      token_type: account.token_type ?? null,
      scope: account.scope ?? null,
      id_token: account.id_token ?? null,
      // If the schema has this column:
      ...(Object.keys(prisma.account.fields ?? {}).includes("session_state")
        ? { session_state: account.session_state ?? null }
        : {}),
    };

    // Never spread the entire "account" to avoid refresh_expires_in, not-before-policy, ...
    return prisma.account.create({ data: allowed });
  },
};

const handler = NextAuth({
  providers: [
    Keycloak({
      issuer: ISSUER,
      clientId: CLIENT_ID,
      clientSecret: CLIENT_SECRET,

      // If you ever encounter OAuthAccountNotLinked in dev, you can temporarily enable it (not recommended for prod)
      allowDangerousEmailAccountLinking: true,

      authorization: { params: { scope: "openid profile email roles" } },
      checks: ["pkce", "state"],
      profile(p) {
        return {
          id: p.sub,
          name: p.name || p.preferred_username,
          email: p.email,
        };
      },
    }),
  ],
  adapter, // use adapter sanitized
  session: { strategy: "jwt" },

  callbacks: {
    async jwt({ token, account }) {
      // First login -> NextAuth + Adapter will save refresh_token to Account.*
      if (account?.access_token) {
        token.accessToken = account.access_token;
        token.idToken = account.id_token;
        token.expiresAt =
          typeof account.expires_at === "number"
            ? account.expires_at
            : Math.floor(Date.now() / 1000) + (account.expires_in ?? 300);
        token.provider = account.provider;
        token.providerAccountId = account.providerAccountId;
      }

      // Update roles from current access token (including F5 page)
      const src = account?.access_token ?? token.accessToken;
      token.roles = extractRolesFromAT(src);

      // AT not expired -> continue using
      if (
        token.expiresAt &&
        Math.floor(Date.now() / 1000) < token.expiresAt - 30
      ) {
        return token;
      }

      // AT expired -> refresh with refresh_token in DB
      if (!token.provider || !token.providerAccountId) return token;

      const acc = await prisma.account.findFirst({
        where: {
          provider: String(token.provider),
          providerAccountId: String(token.providerAccountId),
        },
        select: { id: true, refresh_token: true },
      });

      if (!acc?.refresh_token) {
        token.accessToken = undefined;
        token.expiresAt = 0; // forced to log in again
        return token;
      }

      try {
        const fresh = await refreshWithKeycloak(acc.refresh_token);
        const newExpiresAt = Math.floor(Date.now() / 1000) + fresh.expires_in;

        await prisma.account.update({
          where: { id: acc.id },
          data: {
            access_token: fresh.access_token,
            refresh_token: fresh.refresh_token ?? acc.refresh_token, // rotation if any
            expires_at: newExpiresAt,
          },
        });

        token.accessToken = fresh.access_token;
        token.expiresAt = newExpiresAt;
        token.roles = extractRolesFromAT(fresh.access_token);
      } catch (e) {
        console.error("[refreshWithKeycloak] failed:", e);
        // Mark session expired to force re-login
        token.accessToken = undefined;
        token.idToken = undefined;
        token.expiresAt = 0;
        token.error = e?.code || "RefreshAccessTokenError";
      }

      return token;
    },

    async session({ session, token }) {
      session.accessToken = token.accessToken;
      session.idToken = token.idToken;
      session.roles = token.roles ?? [];

      // === set cookies ===
      const maxAge =
        typeof token.expiresAt === "number"
          ? Math.max(0, token.expiresAt - Math.floor(Date.now() / 1000))
          : 15 * 60;

      if (token.accessToken) {
        setHttpOnlyCookie("access-token", token.accessToken, maxAge);
        if (token.idToken) setHttpOnlyCookie("id-token", token.idToken, maxAge);

        // Save roles as neat base64url
        const roles = Array.isArray(token.roles) ? token.roles : [];
        const rolesB64 = Buffer.from(JSON.stringify(roles), "utf8").toString(
          "base64url"
        );
        setHttpOnlyCookie("app-roles", rolesB64, maxAge);
      } else {
        // => refresh token also expired: delete cookie
        setHttpOnlyCookie("access-token", "", 0);
        setHttpOnlyCookie("id-token", "", 0);
        setHttpOnlyCookie("app-roles", "", 0);
      }

      return session;
    },
  },
});

export { handler as GET, handler as POST };
