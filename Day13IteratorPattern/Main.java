package Day13IteratorPattern;

class Main {

    public static void main(String[] args) {
        System.out.println("=== Iterator Pattern: Library Books ===\n");

        Library library = new Library();

        BookIterator forward = library.forwardIterator();
        BookIterator reverse = library.reverseIterator();

        System.out.println("Forward traversal:");
        printBooks(forward);

        System.out.println("\nReverse traversal (bonus):");
        printBooks(reverse);
    }

    private static void printBooks(BookIterator iterator) {
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
