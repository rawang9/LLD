package Day16ChainOfRes;

/**
 * Request object passed along the chain (not just a raw amount).
 */
public class LoanRequest {

    private final double amount;
    private final int creditScore;
    private final double salary;

    public LoanRequest(double amount, int creditScore, double salary) {
        this.amount = amount;
        this.creditScore = creditScore;
        this.salary = salary;
    }

    public double getAmount() {
        return amount;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return "LoanRequest{amount=" + amount + ", creditScore=" + creditScore + ", salary=" + salary + "}";
    }
}
