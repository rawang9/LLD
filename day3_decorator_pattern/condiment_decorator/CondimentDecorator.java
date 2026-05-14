package day3_decorator_pattern.condiment_decorator;

import day3_decorator_pattern.beverage_base.Beverage;

/**
 * Abstract decorator: wraps a {@link Beverage} and is still a {@code Beverage}
 * so you can stack more decorators on top.
 */
public abstract class CondimentDecorator extends Beverage {
}
