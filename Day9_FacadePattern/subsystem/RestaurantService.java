package Day9_FacadePattern.subsystem;

/**
 * Subsystem: selects restaurant and reserves items for the order.
 * Can be used directly by advanced clients — facade is optional, not mandatory.
 */
public class RestaurantService {

    public void selectRestaurant(String restaurantId, String items) {
        System.out.println("[Restaurant] Selected restaurant " + restaurantId + " for: " + items);
    }
}
