package Day15FlyWeightPattern;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Flyweight factory: returns existing {@link BulletType} for the same intrinsic key.
 * Learning: without this, 100_000 bullets would duplicate image/damage/color data.
 */
public class BulletTypeFactory {

    private static final Map<String, BulletType> cache = new HashMap<>();

    private BulletTypeFactory() {
    }

    public static BulletType getBulletType(String image, String damageType, String color) {
        String key = image + "|" + damageType + "|" + color;
        return cache.computeIfAbsent(key, k -> new BulletType(image, damageType, color));
    }

    /** How many shared flyweight instances exist (should stay small). */
    public static int cachedTypeCount() {
        return cache.size();
    }

    public static Map<String, BulletType> getCacheSnapshot() {
        return Collections.unmodifiableMap(cache);
    }
}
