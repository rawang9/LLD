package day3_decorator_pattern.beverage_base;

/**
 * Concrete component: matcha with no add-ons yet.
 */
public class Matcha extends Beverage {
    @Override
    public int cost() {
        return 10;
    }

    @Override
    public String getDescription() {
        return "Matcha";
    }
}
