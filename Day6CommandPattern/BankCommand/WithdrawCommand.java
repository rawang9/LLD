package Day6CommandPattern.BankCommand;

/**
 * Concrete command: withdraws a fixed amount from a receiver {@link BankAccount}.
 */
public class WithdrawCommand implements Command {

    private final BankAccount account;
    private final int amount;

    public WithdrawCommand(BankAccount account, int amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void execute() {
        account.withdraw(amount);
    }

    @Override
    public void undo() {
        account.deposit(amount);
    }
}
