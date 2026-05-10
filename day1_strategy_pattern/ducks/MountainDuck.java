package ducks;

import strategy.MountainFly;
import strategy.SimpleQuack;

public class MountainDuck extends Duck {
    public MountainDuck() {
        super(new MountainFly(), new SimpleQuack());
    }

    @Override
    public void display() {
        System.out.println("A sturdy mountain duck.");
    }
}
