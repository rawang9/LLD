package Dya17Memento;

/**
 * Memento: immutable snapshot of editor text.
 * Caretaker can store it, but cannot mutate editor internals.
 */
public class EditorMemento {

    private final String textSnapshot;

    public EditorMemento(String textSnapshot) {
        this.textSnapshot = textSnapshot;
    }

    public String getTextSnapshot() {
        return textSnapshot;
    }
}
