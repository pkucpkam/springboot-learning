package com.likelion.utility;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.security.oauth2.jwt.Jwt;

public final class AuthUtility {
    private AuthUtility() {
    }

    public static String optionalStr(String v, String fallback) {
        return (v == null || v.isBlank()) ? fallback : v;
    }

    /** Collect roles from realm_access.roles and resource_access.{client}.roles */
    public static List<String> extractAllRoles(Jwt jwt) {
        Map<String, Object> realmAccess = safeMap(jwt.getClaimAsMap("realm_access"));
        List<String> realm = extractStringListFromNestedMap(realmAccess, "roles");

        Map<String, Object> resAccess = safeMap(jwt.getClaimAsMap("resource_access"));
        List<String> client = new ArrayList<>();
        for (Object v : resAccess.values()) {
            if (v instanceof Map<?, ?> clientMap) {
                client.addAll(extractStringListFromNestedMap(clientMap, "roles"));
            }
        }
        return mergeAndNormalizeRoles(realm, client);
    }

    /** Get List<String> from map[key] typesafely */
    private static List<String> extractStringListFromNestedMap(Map<?, ?> container, String key) {
        if (container == null)
            return List.of();
        Object val = container.get(key); // avoid getOrDefault(...) with wildcardd
        if (!(val instanceof Collection<?> col))
            return List.of();

        List<String> res = new ArrayList<>(col.size());
        for (Object o : col) {
            if (o != null)
                res.add(o.toString());
        }
        return res;
    }

    private static List<String> mergeAndNormalizeRoles(List<String> a, List<String> b) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (a != null)
            set.addAll(a);
        if (b != null)
            set.addAll(b);

        List<String> out = new ArrayList<>(set.size());
        for (String r : set) {
            String withPrefix = (r != null && r.startsWith("ROLE_")) ? r : "ROLE_" + r;
            out.add(withPrefix);
        }
        return out;
    }

    public static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static Map<String, Object> safeMap(Map<String, Object> in) {
        return (in == null) ? Collections.emptyMap() : in;
    }
}
