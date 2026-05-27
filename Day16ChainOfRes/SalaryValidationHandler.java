package Day16ChainOfRes;

/** Salary must be enough to cover EMI-style ratio for loan amount. */
public class SalaryValidationHandler extends LoanHandler {

    @Override
    protected boolean canHandle(LoanRequest request) {
        // e.g. annual salary at least 40% of loan amount
        return request.getSalary() >= request.getAmount() * 0.4;
    }

    @Override
    protected void onReject(LoanRequest request) {
        System.out.println("[SalaryValidationHandler] REJECTED: salary "
                + request.getSalary() + " too low for amount " + request.getAmount());
    }
}
