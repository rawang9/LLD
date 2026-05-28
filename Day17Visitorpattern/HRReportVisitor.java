package Day17Visitorpattern;

/**
 * Bonus: new HR report operation added without modifying Developer, Manager, or Designer.
 */
public class HRReportVisitor implements Visitor {

    @Override
    public void visit(Developer developer) {
        System.out.println("[HR Report] Developer " + developer.getName()
                + " | payroll-ready | engineering track | points="
                + developer.getStoryPointsClosed());
    }

    @Override
    public void visit(Manager manager) {
        System.out.println("[HR Report] Manager " + manager.getName()
                + " | payroll-ready | leadership track | directs="
                + manager.getTeamSize());
    }

    @Override
    public void visit(Designer designer) {
        System.out.println("[HR Report] Designer " + designer.getName()
                + " | payroll-ready | design track | designs="
                + designer.getDesignsDelivered());
    }
}
