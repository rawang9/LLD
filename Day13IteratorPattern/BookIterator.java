package Day13IteratorPattern;

/**
 * Iterator: uniform way to traverse books without exposing storage (list/array/queue).
 */
public interface BookIterator {

    boolean hasNext();

  /** Returns current book and advances cursor. */
    String next();
}
