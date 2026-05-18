package Day9_FacadePattern;

import Day9_FacadePattern.subsystem.DeliveryService;
import Day9_FacadePattern.subsystem.NotificationService;
import Day9_FacadePattern.subsystem.PaymentService;
import Day9_FacadePattern.subsystem.RestaurantService;

/**
 * Facade: one simplified API for the client.
 * Orchestrates subsystems — coordinates workflow, does not replace their business logic.
 *
 * Interview note: avoid a "God Object" facade that absorbs all subsystem responsibilities.
 */
public class FoodDeliveryFacade {

    private final RestaurantService restaurantService;
    private final PaymentService paymentService;
    private final DeliveryService deliveryService;
    private final NotificationService notificationService;

    public FoodDeliveryFacade() {
        this.restaurantService = new RestaurantService();
        this.paymentService = new PaymentService();
        this.deliveryService = new DeliveryService();
        this.notificationService = new NotificationService();
    }

    /**
     * Single entry point for placing an order.
     * Client no longer needs to know subsystem call order.
     */
    public String placeOrder(String customerId, String restaurantId, String items, double amount) {
        String orderId = "ORD-" + System.currentTimeMillis();

        restaurantService.selectRestaurant(restaurantId, items);
        paymentService.pay(amount);
        deliveryService.assignPartner(orderId);
        notificationService.send(customerId, "Order " + orderId + " placed successfully.");

        return orderId;
    }

    // Extension (interview): add LoyaltyService.applyPoints() here without changing client API.
}
