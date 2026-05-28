package Dya17Memento;

import java.util.Stack;

/**
 * Caretaker: manages undo/redo stacks.
 */
public class HistoryManager {

    private final Stack<EditorMemento> undoStack = new Stack<>();
    private final Stack<EditorMemento> redoStack = new Stack<>();

    /**
     * Save a new snapshot into undo stack and clear redo history.
     * Learning: after new save, old redo path is invalid.
     */
    public void saveSnapshot(EditorMemento snapshot) {
        undoStack.push(snapshot);
        redoStack.clear();
    }

    public EditorMemento currentSnapshot() {
        return undoStack.peek();
    }

    public boolean canUndo() {
        // Keep at least one snapshot so editor always has a valid state.
        return undoStack.size() > 1;
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public EditorMemento undo() {
        if (!canUndo()) {
            return currentSnapshot();
        }
        redoStack.push(undoStack.pop());
        return currentSnapshot();
    }

    public EditorMemento redo() {
        if (!canRedo()) {
            return currentSnapshot();
        }
        EditorMemento restored = redoStack.pop();
        undoStack.push(restored);
        return restored;
    }
}
