package Day17Visitorpattern;

/**
 * Element: declares accept() so visitors can run type-specific logic (double dispatch).
 */
public interface Employee {

    void accept(Visitor visitor);

    String getName();
}
