package Day17Visitorpattern;

public class BonusVisitor implements Visitor {

    @Override
    public void visit(Developer developer) {
        double bonus = developer.getStoryPointsClosed() * 100;
        System.out.println("[Bonus] " + developer.getName() + " -> $" + bonus);
    }

    @Override
    public void visit(Manager manager) {
        double bonus = manager.getTeamSize() * 1500;
        System.out.println("[Bonus] " + manager.getName() + " -> $" + bonus);
    }

    @Override
    public void visit(Designer designer) {
        double bonus = designer.getDesignsDelivered() * 200;
        System.out.println("[Bonus] " + designer.getName() + " -> $" + bonus);
    }
}
