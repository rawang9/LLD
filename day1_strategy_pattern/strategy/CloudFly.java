package strategy;

public class CloudFly implements FlyBehavior {
    @Override
    public void fly() {
        System.out.println("Soaring through the clouds...");
    }
}
