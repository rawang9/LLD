package day10_bridge_pattern;

import day10_bridge_pattern.DependentInterfaceEnchantments.FireEnchantment;
import day10_bridge_pattern.DependentInterfaceEnchantments.IceEnchantment;
import day10_bridge_pattern.DependentInterfaceEnchantments.IEnchantments;
import day10_bridge_pattern.IndependentAbstractionWeapons.Gun;
import day10_bridge_pattern.IndependentAbstractionWeapons.Sword;
import day10_bridge_pattern.IndependentAbstractionWeapons.Weapons;

class Main {

    public static void main(String[] args) {
        System.out.println("=== Bridge Pattern: Gaming Weapon System ===\n");

        /*
         * WITHOUT Bridge (bad — class explosion):
         *   FireSword, IceSword, FireGun, IceGun  → 2 weapons × 2 enchantments = 4 classes
         *   Add Poison? 2 more. Add Bow? 2 more. Grows as M × N.
         *
         * WITH Bridge (good — compose at runtime):
         *   Sword + FireEnchantment, Gun + IceEnchantment, etc.
         *   Only 2 weapon classes + 2 enchantment classes = 4 classes, any mix works.
         */

        Weapons swordWithFire = new Sword(new FireEnchantment());
        Weapons swordWithIce = new Sword(new IceEnchantment());
        Weapons gunWithFire = new Gun(new FireEnchantment());
        Weapons gunWithIce = new Gun(new IceEnchantment());

        attack(swordWithFire);
        attack(swordWithIce);
        attack(gunWithFire);
        attack(gunWithIce);

        System.out.println("\n--- Swap enchantment: new impl, no new Sword class ---");
        IEnchantments poison = () -> "poison enchantment";
        attack(new Sword(poison));
    }

    private static void attack(Weapons weapon) {
        weapon.attack();
    }
}
