package day5_singleton_pattern;

class LazySingletonLogger {
    private static LazySingletonLogger instance;

    private LazySingletonLogger() {
        // Private constructor to prevent instantiation
        System.out.println("LazySingletonLogger instance created");
    }

    public static LazySingletonLogger getInstance() {
        if (instance == null) {
            instance = new LazySingletonLogger();
        }
        return instance;
    }

    public void log(String message) {
        System.out.println("Logging message: " + message);
    }
}
