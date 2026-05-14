package day5_singleton_pattern;

class TwoWayThreadSafe {
    private static volatile TwoWayThreadSafe instance;

    private TwoWayThreadSafe() {
        // Private constructor to prevent instantiation
        System.out.println("TwoWayThreadSafe instance created");
    }

    public static TwoWayThreadSafe getInstance() {
        if (instance == null) {
            synchronized (TwoWayThreadSafe.class) {
                if (instance == null) {
                    instance = new TwoWayThreadSafe();
                }
            }
        }
        return instance;
    }

    public void log(String message) {
        System.out.println("Logging message: " + message);
    }
}
