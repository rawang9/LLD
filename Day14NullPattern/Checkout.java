package Day14NullPattern;

/**
 * Client code: always holds a PaymentProcessor (never null).
 * Defaults to NoPaymentProcessor until user picks a method.
 */
public class Checkout {

    private PaymentProcessor processor = new NoPaymentProcessor();

    public void selectPayment(PaymentProcessor processor) {
        this.processor = processor;
    }

    public void checkout() {
        // No null check — Null Object pattern
        processor.pay();
    }
}
