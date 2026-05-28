package Day17Visitorpattern;

public class Manager implements Employee {

    private final String name;
    private final double baseSalary;
    private final int teamSize;

    public Manager(String name, double baseSalary, int teamSize) {
        this.name = name;
        this.baseSalary = baseSalary;
        this.teamSize = teamSize;
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

    public int getTeamSize() {
        return teamSize;
    }
}
