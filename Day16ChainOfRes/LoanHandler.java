package Day16ChainOfRes;

/**
 * Base handler: builds chain and forwards request, or stops on reject.
 *
 * Learning fix: was ILoanHandler with broken links (Manager never pointed to Logging,
 * setNextHandler() called with no args in Main).
 */
public abstract class LoanHandler {

    private LoanHandler next;

    /** Fluent wiring: credit.setNext(salary).setNext(manager)... */
    public LoanHandler setNext(LoanHandler next) {
        this.next = next;
        return next;
    }

    public void handle(LoanRequest request) {
        if (!canHandle(request)) {
            onReject(request);
            return; // stop chain — do not call next
        }
        onPass(request);
        if (next != null) {
            next.handle(request);
        }
    }

    protected abstract boolean canHandle(LoanRequest request);

    protected void onPass(LoanRequest request) {
        System.out.println("[" + handlerName() + "] passed");
    }

    protected void onReject(LoanRequest request) {
        System.out.println("[" + handlerName() + "] REJECTED -> chain stopped");
    }

    private String handlerName() {
        return getClass().getSimpleName();
    }
}
