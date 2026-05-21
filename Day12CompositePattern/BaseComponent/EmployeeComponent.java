package Day12CompositePattern.BaseComponent;

/**
 * Component: common interface for leaf (Developer, Designer) and composite (Manager).
 * Client treats individual employees and whole teams the same way.
 */
public interface EmployeeComponent {

    /** Print this node; depth used for indented hierarchy (recursive composite). */
    void showDetails(int depth);

    /** Client entry point — starts recursion from root at depth 0. */
    default void showDetails() {
        showDetails(0);
    }
}
