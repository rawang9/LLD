package Day17Visitorpattern;

public class Developer implements Employee {

    private final String name;
    private final double baseSalary;
    private final int storyPointsClosed;

    public Developer(String name, double baseSalary, int storyPointsClosed) {
        this.name = name;
        this.baseSalary = baseSalary;
        this.storyPointsClosed = storyPointsClosed;
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

    public int getStoryPointsClosed() {
        return storyPointsClosed;
    }
}
