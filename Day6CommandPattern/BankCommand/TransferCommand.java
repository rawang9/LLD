package Day6CommandPattern.BankCommand;

/**
 * Concrete command: moves a fixed amount from one {@link BankAccount} to another.
 */
public class TransferCommand implements Command {

    private final BankAccount from;
    private final BankAccount to;
    private final int amount;

    public TransferCommand(BankAccount from, BankAccount to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    @Override
    public void execute() {
        from.withdraw(amount);
        to.deposit(amount);
    }

    @Override
    public void undo() {
        to.withdraw(amount);
        from.deposit(amount);
    }
}
