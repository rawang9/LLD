package Day15ProtoTypePattern;

/**
 * Concrete prototype: clone via copy constructor logic — faster than new GameCharacter(...) from scratch
 * when setup is expensive (here simulated with a Weapon object).
 */
public class GameCharacter implements GameCharacterPrototype {

    private String name;
    private Weapon weapon;
    private String armor;

    public GameCharacter(String name, Weapon weapon, String armor) {
        this.name = name;
        this.weapon = weapon;
        this.armor = armor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    /**
     * Deep copy (correct prototype): clone gets its own Weapon — safe to mutate independently.
     */
    @Override
    public GameCharacter cloneCharacter() {
        return new GameCharacter(this.name, this.weapon.copy(), this.armor);
    }

    /**
     * Shallow copy (interview demo only): shares same Weapon reference — mutations leak to prototype.
     */
    public GameCharacter cloneCharacterShallow() {
        return new GameCharacter(this.name, this.weapon, this.armor);
    }

    @Override
    public String toString() {
        return "GameCharacter{name='" + name + "', weapon=" + weapon + ", armor='" + armor + "'}";
    }
}
