package Day16ChainOfRes;

class Main {

    public static void main(String[] args) {
        System.out.println("=== Chain of Responsibility: Loan Approval ===\n");

        LoanHandler chain = new CreditScoreHandler();
        chain.setNext(new SalaryValidationHandler())
                .setNext(new ManagerApprovalHandler())
                .setNext(new LoggingHandler());

        System.out.println("1) Approved request:");
        chain.handle(new LoanRequest(50_000, 720, 80_000));

        System.out.println("\n2) Rejected at credit (chain stops):");
        chain.handle(new LoanRequest(50_000, 600, 80_000));

        System.out.println("\n3) Rejected at salary:");
        chain.handle(new LoanRequest(200_000, 700, 30_000));

        System.out.println("\n4) Large loan — manager rejects:");
        chain.handle(new LoanRequest(150_000, 700, 200_000));
    }
}
