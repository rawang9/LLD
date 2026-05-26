package Day15FlightWeightPattern;

/**
 * Flyweight (intrinsic / shared state): same for all bullets of this type.
 * Created once and reused — image, damageType, color are expensive to store per bullet.
 */
public final class BulletType {

    private final String image;
    private final String damageType;
    private final String color;

    public BulletType(String image, String damageType, String color) {
        this.image = image;
        this.damageType = damageType;
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public String getDamageType() {
        return damageType;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "BulletType{image='" + image + "', damage=" + damageType + ", color=" + color + "}";
    }
}
