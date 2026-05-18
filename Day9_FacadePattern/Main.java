package Day9_FacadePattern;

class Main {

    public static void main(String[] args) {
        System.out.println("=== Facade Pattern: Food Delivery ===\n");

        // Client uses one API — no direct subsystem orchestration
        FoodDeliveryFacade delivery = new FoodDeliveryFacade();
        String orderId = delivery.placeOrder("customer-42", "PizzaHub", "Margherita x2", 24.99);

        System.out.println("\nOrder completed. Id: " + orderId);

        // Subsystems can still be used directly if needed (facade is not mandatory)
        System.out.println("\n--- Optional: bypass facade (advanced use) ---");
        new Day9_FacadePattern.subsystem.RestaurantService()
                .selectRestaurant("BurgerKing", "Whopper");
    }
}
