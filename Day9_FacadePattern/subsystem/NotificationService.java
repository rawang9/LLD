package Day9_FacadePattern.subsystem;

/**
 * Subsystem: notifies customer about order status.
 */
public class NotificationService {

    public void send(String customerId, String message) {
        System.out.println("[Notification] Sent to " + customerId + ": " + message);
    }
}
