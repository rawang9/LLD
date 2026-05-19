package day10_bridge_pattern.IndependentAbstractionWeapons;

import day10_bridge_pattern.DependentInterfaceEnchantments.IEnchantments;

/**
 * Abstraction (bridge side): weapon type.
 *
 * Holds a reference to the Implementor ({@link IEnchantments}) — this IS the bridge.
 * Weapon and enchantment vary independently; you compose them at runtime.
 */
public abstract class Weapons {

    // Bridge field: abstraction delegates behavior to implementation
    protected final IEnchantments enchantment;

    public Weapons(IEnchantments enchantment) {
        this.enchantment = enchantment;
    }

    /**
     * Learning fix: was empty {@code attack(){};} — subclasses must override.
     * Use abstract so every weapon defines its own attack style.
     */
    public abstract void attack();
}
