package Day15ProtoTypePattern;

class Main {

    public static void main(String[] args) {
        System.out.println("=== Prototype Pattern: Character Creation ===\n");

        // Build once — expensive "base" warrior template
        GameCharacter baseWarrior = new GameCharacter(
                "Base Warrior",
                new Weapon("Iron Sword"),
                "Plate Armor");

        // Clone without rebuilding entire setup
        GameCharacter warrior1 = baseWarrior.cloneCharacter();
        warrior1.setName("Warrior 1");

        GameCharacter warrior2 = baseWarrior.cloneCharacter();
        warrior2.setName("Warrior 2");

        GameCharacter warrior3 = baseWarrior.cloneCharacter();
        warrior3.setName("Warrior 3");

        System.out.println("Base:    " + baseWarrior);
        System.out.println("Clone 1: " + warrior1);
        System.out.println("Clone 2: " + warrior2);
        System.out.println("Clone 3: " + warrior3);

        System.out.println("\n=== BONUS: Deep copy solution ===");
        GameCharacter deepBase = new GameCharacter("Deep Base", new Weapon("Bow"), "Chainmail");
        GameCharacter deepClone = deepBase.cloneCharacter();
        deepClone.setName("Deep Clone");
        deepClone.getWeapon().setName("Shadow Blade"); // only clone's Weapon changes

        System.out.println("After deep clone changes weapon:");
        System.out.println("  deepBase  -> " + deepBase);   // unchanged
        System.out.println("  deepClone -> " + deepClone);

        System.out.println("\n=== BONUS: Shallow copy problem ===");
        GameCharacter shallowBase = new GameCharacter("Shallow Base", new Weapon("Spear"), "Leather");
        GameCharacter shallowClone = shallowBase.cloneCharacterShallow();
        shallowClone.setName("Shallow Clone");
        shallowClone.getWeapon().setName("Dragon Slayer"); // mutates shared Weapon!

        System.out.println("After shallow clone changes weapon:");
        System.out.println("  shallowBase  -> " + shallowBase);   // weapon changed too!
        System.out.println("  shallowClone -> " + shallowClone);
    }
}
