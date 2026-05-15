package Day6CommandPattern.BankCommand;

/**
 * Receiver: holds balance and performs the actual banking operations.
 * Commands delegate here; the invoker does not call these methods directly.
 */
public class BankAccount {

    private int balance;

    public BankAccount(int balance) {
        this.balance = balance;
    }

    public void deposit(int amount) {
        balance += amount;
        System.out.println("Deposited " + amount + " to account. New balance: " + balance);
    }

    public void withdraw(int amount) {
        balance -= amount;
        System.out.println("Withdrawn " + amount + " from account. New balance: " + balance);
    }

    public int getBalance() {
        return balance;
    }
}
