package Day15FlyWeightPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class Main {

    private static final List<String> COLORS = List.of("red", "green");
    private static final List<String> DAMAGE_TYPES = List.of("fire", "ice");
    private static final String IMAGE = "bullet_icon.png";

    public static void main(String[] args) {
        System.out.println("=== Flyweight Pattern: Bullet Rendering ===\n");

        List<Bullet> bullets = new ArrayList<>();
        int totalBullets = 100_000;

        for (int i = 0; i < totalBullets; i++) {
            int variant = ThreadLocalRandom.current().nextInt(COLORS.size());
            int x = ThreadLocalRandom.current().nextInt(0, 1920);
            int y = ThreadLocalRandom.current().nextInt(0, 1080);
            int velocity = ThreadLocalRandom.current().nextInt(1, 50);

            bullets.add(new Bullet(
                    x, y, velocity,
                    IMAGE,
                    DAMAGE_TYPES.get(variant),
                    COLORS.get(variant)));
        }

        System.out.println("Bullets created: " + bullets.size());
        System.out.println("Shared BulletType instances (flyweight cache): "
                + BulletTypeFactory.cachedTypeCount());
        System.out.println("(Without flyweight you'd store image/damage/color "
                + totalBullets + " times)\n");

        System.out.println("Cached types:");
        BulletTypeFactory.getCacheSnapshot().values()
                .forEach(type -> System.out.println("  " + type));

        System.out.println("\nSample renders (first 3 bullets):");
        bullets.get(0).render();
        bullets.get(1).render();
        bullets.get(2).render();

        System.out.println("\nFlyweight identity check (same intrinsic key -> same object):");
        Bullet extra = new Bullet(0, 0, 1, IMAGE, DAMAGE_TYPES.get(0), COLORS.get(0));
        System.out.println("  bullet[0].type == new fire/red bullet.type -> "
                + (bullets.get(0).getType() == extra.getType()));
    }
}
