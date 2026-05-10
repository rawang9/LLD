package strategy;

public class SimpleQuack implements QuackBehavior {
    @Override
    public void quack() {
        System.out.println("Quack!");
    }
}
