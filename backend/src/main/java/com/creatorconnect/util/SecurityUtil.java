package com.creatorconnect.util;

import com.creatorconnect.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Small helper for pulling the authenticated user's id/role out of the SecurityContext. */
public final class SecurityUtil {

    private SecurityUtil() {}

    public static Long currentUserId() {
        CustomUserDetails principal = currentPrincipal();
        return principal == null ? null : principal.getUserId();
    }

    public static String currentEmail() {
        CustomUserDetails principal = currentPrincipal();
        return principal == null ? null : principal.getUsername();
    }

    public static CustomUserDetails currentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return null;
        }
        return (CustomUserDetails) auth.getPrincipal();
    }
}
