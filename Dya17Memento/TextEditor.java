package Dya17Memento;

/**
 * Originator: owns mutable editor text and creates/restores snapshots.
 */
public class TextEditor {

    private String text;
    private final HistoryManager history;

    public TextEditor() {
        this.text = "";
        this.history = new HistoryManager();
        // Initial empty snapshot enables safe multi-undo.
        save();
    }

    // Requirement alias: write()
    public void write(String newText) {
        text += newText;
    }

    // Backward compatibility with previous method name.
    public void type(String newText) {
        write(newText);
    }

    public void print() {
        System.out.println(text);
    }

    /** Originator creates memento from current state. */
    public EditorMemento save() {
        EditorMemento snapshot = new EditorMemento(text);
        history.saveSnapshot(snapshot);
        return snapshot;
    }

    /** Originator restores from memento. */
    public void restore(EditorMemento memento) {
        text = memento.getTextSnapshot();
    }

    public void undo() {
        restore(history.undo());
    }

    public void redo() {
        restore(history.redo());
    }

    public String getText() {
        return text;
    }
}
