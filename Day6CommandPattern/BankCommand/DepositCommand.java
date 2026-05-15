package Day6CommandPattern.BankCommand;

/**
 * Concrete command: deposits a fixed amount into a receiver {@link BankAccount}.
 */
public class DepositCommand implements Command {

    private final BankAccount account;
    private final int amount;

    public DepositCommand(BankAccount account, int amount) {
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void execute() {
        account.deposit(amount);
    }

    @Override
    public void undo() {
        account.withdraw(amount);
    }
}
