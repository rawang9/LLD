package Day13IteratorPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate: owns the collection and creates iterators.
 *
 * Learning: client must NOT call getBookInv() / get list directly — use iterators only.
 */
public class Library {

    private final List<String> books = new ArrayList<>();

    public Library() {
        books.add("Krish 1");
        books.add("Krish 2");
        books.add("Krish 3");
    }

    public BookIterator forwardIterator() {
        return new LibraryForwardIterator(this);
    }

    /** Bonus: backward traversal with same BookIterator interface. */
    public BookIterator reverseIterator() {
        return new LibraryReverseIterator(this);
    }

    // Package-private: only iterators in this package touch internal storage
    int bookCount() {
        return books.size();
    }

    String bookAt(int index) {
        return books.get(index);
    }
}
