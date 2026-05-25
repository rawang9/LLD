package Day14NullPattern;

/**
 * Common interface for real payments and the null object.
 * Client always calls pay() — no null checks required.
 */
public interface PaymentProcessor {

    void pay();
}
