package Day17Visitorpattern;

public class PerformanceVisitor implements Visitor {

    @Override
    public void visit(Developer developer) {
        String rating = developer.getStoryPointsClosed() >= 20 ? "Exceeds" : "Meets";
        System.out.println("[Performance] " + developer.getName() + " -> " + rating
                + " (points=" + developer.getStoryPointsClosed() + ")");
    }

    @Override
    public void visit(Manager manager) {
        String rating = manager.getTeamSize() >= 8 ? "Exceeds" : "Meets";
        System.out.println("[Performance] " + manager.getName() + " -> " + rating
                + " (teamSize=" + manager.getTeamSize() + ")");
    }

    @Override
    public void visit(Designer designer) {
        String rating = designer.getDesignsDelivered() >= 10 ? "Exceeds" : "Meets";
        System.out.println("[Performance] " + designer.getName() + " -> " + rating
                + " (designs=" + designer.getDesignsDelivered() + ")");
    }
}
