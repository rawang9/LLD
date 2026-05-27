package Day16ChainOfRes;

/** Validates minimum credit score. */
public class CreditScoreHandler extends LoanHandler {

    private static final int MIN_CREDIT_SCORE = 650;

    @Override
    protected boolean canHandle(LoanRequest request) {
        return request.getCreditScore() >= MIN_CREDIT_SCORE;
    }

    @Override
    protected void onReject(LoanRequest request) {
        System.out.println("[CreditScoreHandler] REJECTED: credit score "
                + request.getCreditScore() + " < " + MIN_CREDIT_SCORE);
    }
}
