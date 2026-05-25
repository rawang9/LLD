package Day13IteratorPattern;

/**
 * Bonus — ReverseIterator: same BookIterator API, walks from last book to first.
 *
 * Learning: one interface, multiple traversal strategies; client code stays identical.
 */
public class LibraryReverseIterator implements BookIterator {

    private final Library library;
    private int currentIndex;

    public LibraryReverseIterator(Library library) {
        this.library = library;
        this.currentIndex = library.bookCount() - 1;
    }

    @Override
    public boolean hasNext() {
        return currentIndex >= 0;
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more books in reverse direction");
        }
        return library.bookAt(currentIndex--);
    }
}
