package Day12CompositePattern;

import Day12CompositePattern.BaseComponent.EmployeeComponent;
import Day12CompositePattern.Composite.Manager;
import Day12CompositePattern.leafs.Designer;
import Day12CompositePattern.leafs.Developer;

class Main {

    public static void main(String[] args) {
        System.out.println("=== Composite Pattern: Organization Hierarchy ===\n");

        EmployeeComponent designer1 = new Designer("Shiva");
        EmployeeComponent designer2 = new Designer("Glen");
        EmployeeComponent developer1 = new Developer("Kuldeep");
        EmployeeComponent developer2 = new Developer("Ankur");

        Manager designManager = new Manager("Jeevan");
        designManager.addEmployee(designer1);
        designManager.addEmployee(designer2);

        Manager platformManager = new Manager("Karthik");
        platformManager.addEmployee(developer1);
        platformManager.addEmployee(developer2);
        platformManager.addEmployee(designManager);

        EmployeeComponent orgRoot = platformManager;
        orgRoot.showDetails();
    }
}
