package Day13IteratorPattern;

/**
 * Concrete iterator: forward traversal with currentIndex.
 */
public class LibraryForwardIterator implements BookIterator {

    private final Library library;
    private int currentIndex;

    public LibraryForwardIterator(Library library) {
        this.library = library;
        this.currentIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < library.bookCount();
    }

    @Override
    public String next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more books in forward direction");
        }
        return library.bookAt(currentIndex++);
    }
}
