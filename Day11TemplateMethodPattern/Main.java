package Day11TemplateMethodPattern;

class Main {

    public static void main(String[] args) {
        System.out.println("=== Template Method: Online Order Processing ===\n");

        OrderProcessingTemplate digital = new DigitalOrder();
        OrderProcessingTemplate physical = new PhysicalOrder();

        System.out.println("--- Digital order ---");
        digital.processOrder();

        System.out.println("\n--- Physical order ---");
        physical.processOrder();
    }
}
