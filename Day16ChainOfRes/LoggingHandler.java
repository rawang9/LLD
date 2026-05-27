package Day16ChainOfRes;

/**
 * Terminal handler: logs successful approval after all prior checks passed.
 * No next handler required.
 */
public class LoggingHandler extends LoanHandler {

    @Override
    protected boolean canHandle(LoanRequest request) {
        return true; // always logs if chain reaches here
    }

    @Override
    protected void onPass(LoanRequest request) {
        System.out.println("[LoggingHandler] APPROVED and logged: " + request);
    }

    @Override
    protected void onReject(LoanRequest request) {
        // should not reject if reached
    }
}
