package Day15ProtoTypePattern;

/**
 * Mutable reference type — used to demonstrate shallow vs deep copy in interviews.
 */
public class Weapon {

    private String name;

    public Weapon(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** Deep copy helper: new Weapon instance with same name. */
    public Weapon copy() {
        return new Weapon(this.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
