// middleware.ts
import { getToken } from "next-auth/jwt";
import { NextResponse } from "next/server";

const PROTECTED = ["/dashboard", "/customers"];

function redirectToLogin(req) {
  const url = req.nextUrl.clone();
  url.pathname = "/login";
  url.searchParams.set("next", req.nextUrl.pathname + req.nextUrl.search);

  // Best-effort: clear old cookies to avoid loops
  const res = NextResponse.redirect(url);
  res.cookies.set("access-token", "", { path: "/", httpOnly: true, maxAge: 0 });
  res.cookies.set("id-token", "", { path: "/", httpOnly: true, maxAge: 0 });
  res.cookies.set("app-roles", "", { path: "/", httpOnly: true, maxAge: 0 });
  // next-auth session cookies (depending on the environment, there will be 1 of 2)
  res.cookies.set("next-auth.session-token", "", { path: "/", maxAge: 0 });
  res.cookies.set("__Secure-next-auth.session-token", "", {
    path: "/",
    maxAge: 0,
  });
  return res;
}

export async function middleware(req) {
  const { pathname } = req.nextUrl;

  // Protect only the necessary paths
  const needAuth = PROTECTED.some((p) => pathname.startsWith(p));
  if (!needAuth) return NextResponse.next();

  // Avoid auto-blocking of login page & NextAuth flow
  const isAuthPage =
    pathname.startsWith("/login") || pathname.startsWith("/api/auth");
  if (isAuthPage) return NextResponse.next();

  // 1) Read NextAuth JWT (by encoding the accessToken/expiresAt field into the token)
  const jwt = await getToken({ req, secret: process.env.NEXTAUTH_SECRET });

  // No JWT, or callbacks.jwt has accessToken = undefined / expiresAt = 0 (refresh fail)
  if (
    !jwt ||
    !("accessToken" in jwt) ||
    !jwt.accessToken ||
    jwt.expiresAt === 0
  ) {
    return redirectToLogin(req);
  }

  // 2) Fallback: if the access-token cookie is no longer there (callbacks.session deleted after refresh failed)
  const atCookie = req.cookies.get("access-token")?.value;
  if (!atCookie) {
    return redirectToLogin(req);
  }

  // Here: have JWT + have cookie -> let it go (even though AT may have expired, the page will call /api/auth/session to refresh)
  return NextResponse.next();
}

// Applies only to protected routes
export const config = {
  matcher: PROTECTED.map((p) => `${p}/:path*`),
};
