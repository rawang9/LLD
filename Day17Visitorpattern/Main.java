package Day17Visitorpattern;

import java.util.List;

class Main {

    public static void main(String[] args) {
        System.out.println("=== Visitor Pattern: Employee Analytics ===\n");

        List<Employee> employees = List.of(
                new Developer("Kuldeep", 80_000, 24),
                new Manager("Karthik", 120_000, 10),
                new Designer("Shiva", 75_000, 12));

        runVisitor(employees, new SalaryVisitor(), "Salary");
        runVisitor(employees, new PerformanceVisitor(), "Performance");
        runVisitor(employees, new BonusVisitor(), "Bonus");
        runVisitor(employees, new HRReportVisitor(), "HR Report");
    }

    private static void runVisitor(List<Employee> employees, Visitor visitor, String title) {
        System.out.println("--- " + title + " ---");
        for (Employee employee : employees) {
            employee.accept(visitor);
        }
        System.out.println();
    }
}
