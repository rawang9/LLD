package Day12CompositePattern.Composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Day12CompositePattern.BaseComponent.EmployeeComponent;

/**
 * Composite: can hold developers, designers, and other managers (nested hierarchy).
 *
 * Learning: addEmployee/removeEmployee are NOT on EmployeeComponent — only composites
 * need them. Client uses Manager to build tree, then EmployeeComponent to display it.
 */
public class Manager implements EmployeeComponent {

    private final String nameAndTitle;
    private final List<EmployeeComponent> reports = new ArrayList<>();

    public Manager(String name) {
        this.nameAndTitle = name + " (Manager)";
    }

    public void addEmployee(EmployeeComponent employee) {
        reports.add(employee);
    }

    public void removeEmployee(EmployeeComponent employee) {
        reports.remove(employee);
    }

    @Override
    public void showDetails(int depth) {
        System.out.println(indent(depth) + nameAndTitle);
        // Recursive: delegate to each child — leaf or another Manager
        for (EmployeeComponent report : reports) {
            report.showDetails(depth + 1);
        }
    }

    public List<EmployeeComponent> getReports() {
        return Collections.unmodifiableList(reports);
    }

    private static String indent(int depth) {
        return "  ".repeat(depth);
    }
}
