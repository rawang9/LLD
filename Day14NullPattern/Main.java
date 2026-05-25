package Day14NullPattern;

class Main {

    public static void main(String[] args) {
        System.out.println("=== Null Object Pattern: Payment Processing ===\n");

        Checkout checkout = new Checkout();

        System.out.println("1) No method selected (null object):");
        checkout.checkout();

        System.out.println("\n2) Credit card selected:");
        checkout.selectPayment(new CreditCardPayment());
        checkout.checkout();

        System.out.println("\n3) UPI selected:");
        checkout.selectPayment(new UpiPayment());
        checkout.checkout();

        System.out.println("\n4) Silent null object (no-op):");
        Checkout silentCheckout = new Checkout();
        silentCheckout.selectPayment(NoPaymentProcessor.silent());
        silentCheckout.checkout();
        System.out.println("(no output — silent no-op)");
    }
}
