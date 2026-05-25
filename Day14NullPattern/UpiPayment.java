package Day14NullPattern;

/** Real object: UPI payment. */
public class UpiPayment implements PaymentProcessor {

    @Override
    public void pay() {
        System.out.println("Processing UPI payment...");
    }
}
