package Day9_FacadePattern.subsystem;

/**
 * Subsystem: assigns a delivery partner after order is confirmed.
 */
public class DeliveryService {

    public void assignPartner(String orderId) {
        System.out.println("[Delivery] Partner assigned for order " + orderId);
    }
}
