package day3_decorator.beverage_base;

/**
 * Concrete component: cappuccino with no add-ons yet.
 */
public class Cappuccino extends Beverage {
    @Override
    public int cost() {
        return 15;
    }

    @Override
    public String getDescription() {
        return "Cappuccino";
    }
}
