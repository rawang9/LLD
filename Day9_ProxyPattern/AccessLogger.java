package Day9_ProxyPattern;

/**
 * Audit logging before delegating to the real object.
 */
public final class AccessLogger {

    private AccessLogger() {
    }

    public static void log(String userId, String documentId, boolean granted) {
        System.out.println("[Audit] user=" + userId + " doc=" + documentId + " granted=" + granted);
    }
}
