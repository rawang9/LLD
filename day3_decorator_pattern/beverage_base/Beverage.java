package day3_decorator_pattern.beverage_base;

/**
 * Component: every drink exposes a price and a description (plain or decorated).
 */
public abstract class Beverage {
    public abstract int cost();

    public abstract String getDescription();
}
