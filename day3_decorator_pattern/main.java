package day3_decorator_pattern;

import day3_decorator_pattern.beverage_base.Beverage;
import day3_decorator_pattern.beverage_base.Cappuccino;
import day3_decorator_pattern.condiment_decorator.ChocolateDecorator;
import day3_decorator_pattern.condiment_decorator.MilkDecorator;

/**
 * Decorator pattern (coffee shop): start with a concrete drink, then wrap it
 * in decorators. Each wrapper adds behavior while still looking like a {@link Beverage}.
 */
class Main {
    public static void main(String[] args) {
        System.out.println("Learning the Decorator pattern");

        // Base drink, then stack decorators (order changes price/description).
        Beverage order = new Cappuccino();
        System.out.println(order.getDescription() + " $" + order.cost());

        order = new MilkDecorator(order);
        System.out.println(order.getDescription() + " $" + order.cost());

        order = new ChocolateDecorator(order);
        System.out.println(order.getDescription() + " $" + order.cost());
    }
}
