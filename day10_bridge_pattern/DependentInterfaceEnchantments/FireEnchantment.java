package day10_bridge_pattern.DependentInterfaceEnchantments;

/** Concrete implementor — add new enchantments without new weapon classes. */
public class FireEnchantment implements IEnchantments {

    @Override
    public String effect() {
        return "fire enchantment";
    }
}
