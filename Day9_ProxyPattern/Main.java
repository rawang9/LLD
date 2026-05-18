package Day9_ProxyPattern;

class Main {

    public static void main(String[] args) {
        System.out.println("=== Proxy Pattern: Document Access ===\n");

        // Client uses Document interface — proxy vs real is transparent
        Document adminDoc = new DocumentProxy("DOC-001", "ADMIN", "alice", "ADMIN");
        Document userDoc = new DocumentProxy("DOC-002", "USER", "bob", "USER");
        Document deniedDoc = new DocumentProxy("DOC-003", "ADMIN", "bob", "USER");

        System.out.println("--- Admin opens ADMIN-level doc ---");
        adminDoc.open();

        System.out.println("\n--- User opens USER-level doc ---");
        userDoc.open();

        System.out.println("\n--- User denied on ADMIN-level doc (no lazy load) ---");
        deniedDoc.open();
        System.out.println("(Notice: no [RealDocument] Loaded line for denied access)");
    }
}
