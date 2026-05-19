package day10_bridge_pattern.IndependentAbstractionWeapons;

import day10_bridge_pattern.DependentInterfaceEnchantments.IEnchantments;

/** Refined abstraction: how a gun attacks (independent of fire vs ice). */
public class Gun extends Weapons {

    public Gun(IEnchantments enchantment) {
        super(enchantment);
    }

    @Override
    public void attack() {
        System.out.println("Shooting gun with " + enchantment.effect());
    }
}
