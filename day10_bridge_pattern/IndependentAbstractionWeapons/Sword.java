package day10_bridge_pattern.IndependentAbstractionWeapons;

import day10_bridge_pattern.DependentInterfaceEnchantments.IEnchantments;

/** Refined abstraction: how a sword attacks (independent of fire vs ice). */
public class Sword extends Weapons {

    public Sword(IEnchantments enchantment) {
        super(enchantment);
    }

    @Override
    public void attack() {
        // Delegates enchantment detail to Implementor — no FireSword / IceSword classes
        System.out.println("Waving sword with " + enchantment.effect());
    }
}
