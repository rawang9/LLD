package Day6CommandPattern;

import Day6CommandPattern.BankCommand.Command;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Invoker: runs commands and records them so {@link #undoLastTransaction()} can
 * reverse the most recent operation without the client tracking amounts or types.
 */
public class TransactionManager {

    private final List<Command> history = new ArrayList<>();

    /**
     * Executes the command, then appends it to history for a possible undo.
     */
    public void execute(Command command) {
        command.execute();
        history.add(command);
    }

    /**
     * Pops the last executed command (if any) and calls {@link Command#undo()}.
     */
    public void undoLastTransaction() {
        if (history.isEmpty()) {
            System.out.println("Nothing to undo (history is empty).");
            return;
        }
        Command last = history.remove(history.size() - 1);
        last.undo();
    }

    /** Read-only view of executed commands (newest is at the end). */
    public List<Command> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
