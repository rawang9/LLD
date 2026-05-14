package day3_decorator_pattern.condiment_decorator;

import day3_decorator_pattern.beverage_base.Beverage;

/**
 * Concrete decorator: adds chocolate cost and label to the wrapped drink.
 */
public class ChocolateDecorator extends CondimentDecorator {
    private final Beverage wrapped;

    public ChocolateDecorator(Beverage wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public int cost() {
        return wrapped.cost() + 10;
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription() + " + Chocolate";
    }
}
