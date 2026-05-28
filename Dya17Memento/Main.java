package Dya17Memento;

class Main {
    public static void main(String[] args) {
        System.out.println("=== Memento Pattern: Text Editor Undo/Redo ===\n");

        TextEditor editor = new TextEditor();

        editor.write("Hello");
        editor.save();
        editor.write(" World");
        editor.save();
        editor.write(" !!!");
        editor.save();

        System.out.println("Current:");
        editor.print();

        System.out.println("\nUndo x2:");
        editor.undo();
        editor.print();
        editor.undo();
        editor.print();

        System.out.println("\nRedo x1:");
        editor.redo();
        editor.print();

        System.out.println("\nWrite new text after undo/redo path:");
        editor.write(" from Memento");
        editor.save();
        editor.print();
    }
}
