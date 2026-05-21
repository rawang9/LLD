package Day12CompositePattern.leafs;

import Day12CompositePattern.BaseComponent.EmployeeComponent;

/** Leaf: terminal node in the org tree. */
public class Designer implements EmployeeComponent {

    private final String nameAndTitle;

    public Designer(String name) {
        this.nameAndTitle = name + " (Designer)";
    }

    @Override
    public void showDetails(int depth) {
        System.out.println(indent(depth) + nameAndTitle);
    }

    private static String indent(int depth) {
        return "  ".repeat(depth);
    }
}
