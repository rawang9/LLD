package Day14NullPattern;

/**
 * Null object: stands in when no payment method is selected.
 *
 * Learning: replaces {@code if (processor == null)} with a safe default behavior.
 * Choose message (this) or silent no-op (see {@link #silent()}).
 */
public class NoPaymentProcessor implements PaymentProcessor {

    private final boolean silent;

    public NoPaymentProcessor() {
        this(false);
    }

    /** Silent no-op variant for assignment bonus behavior. */
    public static NoPaymentProcessor silent() {
        return new NoPaymentProcessor(true);
    }

    private NoPaymentProcessor(boolean silent) {
        this.silent = silent;
    }

    @Override
    public void pay() {
        if (!silent) {
            System.out.println("No payment method selected");
        }
        // else: intentional no-op — still valid, no NPE
    }
}
