package Day12CompositePattern.leafs;

import Day12CompositePattern.BaseComponent.EmployeeComponent;

/**
 * Leaf: no children. Cannot add subordinates (addEmployee lives only on Manager).
 */
public class Developer implements EmployeeComponent {

    private final String nameAndTitle;

    public Developer(String name) {
        this.nameAndTitle = name + " (Developer)";
    }

    @Override
    public void showDetails(int depth) {
        System.out.println(indent(depth) + nameAndTitle);
    }

    private static String indent(int depth) {
        return "  ".repeat(depth);
    }
}
