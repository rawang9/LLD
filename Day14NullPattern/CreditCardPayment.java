package Day14NullPattern;

/** Real object: credit card payment. */
public class CreditCardPayment implements PaymentProcessor {

    @Override
    public void pay() {
        System.out.println("Processing credit card payment...");
    }
}
