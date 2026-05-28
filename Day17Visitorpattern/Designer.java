package Day17Visitorpattern;

public class Designer implements Employee {

    private final String name;
    private final double baseSalary;
    private final int designsDelivered;

    public Designer(String name, double baseSalary, int designsDelivered) {
        this.name = name;
        this.baseSalary = baseSalary;
        this.designsDelivered = designsDelivered;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getName() {
        return name;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public int getDesignsDelivered() {
        return designsDelivered;
    }
}
