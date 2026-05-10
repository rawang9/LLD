package ducks;

import strategy.FlyBehavior;
import strategy.QuackBehavior;

public abstract class Duck {
    protected FlyBehavior flyBehavior;
    protected QuackBehavior quackBehavior;

    public Duck(FlyBehavior fly, QuackBehavior quack) {
        this.flyBehavior = fly;
        this.quackBehavior = quack;
    }

    public void fly() {
        flyBehavior.fly();
    }

    public void quack() {
        quackBehavior.quack();
    }

    public abstract void display();

    public void setFlyBehavior(FlyBehavior fly) {
        this.flyBehavior = fly;
    }
}
