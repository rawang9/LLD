package Day16ChainOfRes;

/** Large loans need manager approval threshold. */
public class ManagerApprovalHandler extends LoanHandler {

    private static final double MANAGER_LIMIT = 100_000;

    @Override
    protected boolean canHandle(LoanRequest request) {
        if (request.getAmount() <= MANAGER_LIMIT) {
            return true;
        }
        // simulate manager sign-off for high amounts with good credit
        return request.getCreditScore() >= 750;
    }

    @Override
    protected void onPass(LoanRequest request) {
        if (request.getAmount() > MANAGER_LIMIT) {
            System.out.println("[ManagerApprovalHandler] manager approved large loan");
        } else {
            super.onPass(request);
        }
    }

    @Override
    protected void onReject(LoanRequest request) {
        System.out.println("[ManagerApprovalHandler] REJECTED: amount "
                + request.getAmount() + " needs manager (credit insufficient)");
    }
}
