package Day17Visitorpattern;

/** New operation: salary analytics — no changes to employee classes. */
public class SalaryVisitor implements Visitor {

    @Override
    public void visit(Developer developer) {
        double total = developer.getBaseSalary() + developer.getStoryPointsClosed() * 500;
        System.out.println("[Salary] " + developer.getName() + " (Developer) -> $" + total);
    }

    @Override
    public void visit(Manager manager) {
        double total = manager.getBaseSalary() + manager.getTeamSize() * 2000;
        System.out.println("[Salary] " + manager.getName() + " (Manager) -> $" + total);
    }

    @Override
    public void visit(Designer designer) {
        double total = designer.getBaseSalary() + designer.getDesignsDelivered() * 800;
        System.out.println("[Salary] " + designer.getName() + " (Designer) -> $" + total);
    }
}
