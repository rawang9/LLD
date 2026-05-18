package Day9_ProxyPattern;

/**
 * Access control helper used by the proxy (cross-cutting concern — not inside real document).
 */
public final class AccessValidator {

    private AccessValidator() {
    }

    public static boolean hasAccess(String userRole, String requiredRole) {
        if (userRole == null || requiredRole == null) {
            return false;
        }
        // Simple hierarchy: ADMIN can open anything; USER only USER-level docs
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        return userRole.equals(requiredRole);
    }
}
