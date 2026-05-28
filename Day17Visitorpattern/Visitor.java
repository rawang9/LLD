package Day17Visitorpattern;

/**
 * Visitor: one visit method per concrete element type.
 * Add new operations by adding a new Visitor class — do not change Developer/Manager/Designer.
 */
public interface Visitor {

    void visit(Developer developer);

    void visit(Manager manager);

    void visit(Designer designer);
}
