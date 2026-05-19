package day10_bridge_pattern.DependentInterfaceEnchantments;

/** Concrete implementor — compose with Sword or Gun via constructor injection. */
public class IceEnchantment implements IEnchantments {

    @Override
    public String effect() {
        return "ice enchantment";
    }
}
