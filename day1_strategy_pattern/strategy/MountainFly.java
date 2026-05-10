package strategy;

public class MountainFly implements FlyBehavior {
    @Override
    public void fly() {
        System.out.println("Darting between peaks...");
    }
}
