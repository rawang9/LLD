package ducks;

import strategy.CloudFly;
import strategy.SimpleQuack;

public class CloudDuck extends Duck {
    public CloudDuck() {
        super(new CloudFly(), new SimpleQuack());
    }

    @Override
    public void display() {
        System.out.println("A cloud-shaped duck.");
    }
}
