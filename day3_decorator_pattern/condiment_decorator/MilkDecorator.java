package day3_decorator_pattern.condiment_decorator;

import day3_decorator_pattern.beverage_base.Beverage;

/**
 * Concrete decorator: adds milk cost and label to the wrapped drink.
 */
public class MilkDecorator extends CondimentDecorator {
    private final Beverage wrapped;

    public MilkDecorator(Beverage wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int cost() {
        return wrapped.cost() + 5;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Milk";
    }
}
