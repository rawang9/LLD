package Day9_ProxyPattern;

/**
 * Proxy: same interface as ConfidentialDocument.
 * - Validates role before access
 * - Logs every attempt
 * - Lazily creates real document only on first successful open (virtual proxy)
 */
public class DocumentProxy implements Document {

    private final String documentId;
    private final String requiredRole;
    private final String userId;
    private final String userRole;

    // Lazy: real object created only after access is granted
    private ConfidentialDocument realDocument;

    public DocumentProxy(String documentId, String requiredRole, String userId, String userRole) {
        this.documentId = documentId;
        this.requiredRole = requiredRole;
        this.userId = userId;
        this.userRole = userRole;
    }

    @Override
    public void open() {
        boolean granted = AccessValidator.hasAccess(userRole, requiredRole);
        AccessLogger.log(userId, documentId, granted);

        if (!granted) {
            System.out.println("[Proxy] Access denied for user " + userId + " (role=" + userRole + ")");
            return;
        }

        // Virtual proxy: create expensive real object only when needed
        if (realDocument == null) {
            realDocument = new ConfidentialDocument(documentId);
        }
        realDocument.open();
    }
}
