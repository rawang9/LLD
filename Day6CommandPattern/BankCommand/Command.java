package Day6CommandPattern.BankCommand;

/**
 * Command: declares {@link #execute()} and {@link #undo()} so the invoker can
 * run operations and reverse them without knowing concrete transaction types.
 */
public interface Command {

    void execute();

    void undo();
}
