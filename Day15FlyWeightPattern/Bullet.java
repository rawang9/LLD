package Day15FlyWeightPattern;

/**
 * Context object: extrinsic state (x, y, velocity) varies per bullet.
 * Intrinsic state lives in shared {@link BulletType} reference.
 */
public class Bullet {

    private final int x;
    private final int y;
    private final int velocity;
    private final BulletType type;

    public Bullet(int x, int y, int velocity, String image, String damageType, String color) {
        this.type = BulletTypeFactory.getBulletType(image, damageType, color);
        this.x = x;
        this.y = y;
        this.velocity = velocity;
    }

    /** Render uses shared type + per-bullet position/velocity. */
    public void render() {
        System.out.println("Render " + type.getColor() + " " + type.getDamageType()
                + " bullet at (" + x + "," + y + ") vel=" + velocity
                + " img=" + type.getImage());
    }

    public BulletType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Bullet{pos=(" + x + "," + y + "), velocity=" + velocity + ", type=" + type + "}";
    }
}
