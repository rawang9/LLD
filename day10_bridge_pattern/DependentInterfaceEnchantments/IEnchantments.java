package day10_bridge_pattern.DependentInterfaceEnchantments;

/**
 * Implementor: enchantment behavior (implementation side of the bridge).
 * Abstraction ({@link day10_bridge_pattern.IndependentAbstractionWeapons.Weapons})
 * calls this interface — not concrete Fire/Ice classes directly.
 */
public interface IEnchantments {

    String effect();
}
