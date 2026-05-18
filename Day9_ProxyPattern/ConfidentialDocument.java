package Day9_ProxyPattern;

/**
 * Real object (RealSubject): expensive or sensitive resource.
 * Created lazily by DocumentProxy on first successful access.
 */
public class ConfidentialDocument implements Document {

    private final String documentId;

    public ConfidentialDocument(String documentId) {
        this.documentId = documentId;
        // Simulates heavy load (DB fetch, file read, decryption)
        System.out.println("[RealDocument] Loaded content for " + documentId + " (lazy creation)");
    }

    @Override
    public void open() {
        System.out.println("[RealDocument] Opening confidential content: " + documentId);
    }
}
