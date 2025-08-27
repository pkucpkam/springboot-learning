"use client";
import { useSession } from "next-auth/react";
import { useRouter, useSearchParams } from "next/navigation";
import { useEffect } from "react";
import { signIn } from "next-auth/react";

export default function LoginPage() {
  const { status } = useSession(); // "loading" | "authenticated" | "unauthenticated"
  const router = useRouter();
  const sp = useSearchParams();
  const next = sp.get("next") || "/dashboard";

  useEffect(() => {
    if (status === "authenticated") router.replace(next);
  }, [status, next, router]);

  return (
    <div>
      <h2>Login</h2>
      <button onClick={() => signIn("keycloak", { callbackUrl: next })} style={{marginRight: "5px"}}>
        Continue with Username/Password (Keycloak)
      </button>
      <button
        onClick={() =>
          signIn("keycloak", { callbackUrl: next }, { kc_idp_hint: "google" })
        }
      >
        Continue with Google
      </button>
    </div>
  );
}
