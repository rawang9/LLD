package day5_singleton_pattern;

class EagerSingletonLogger 
{
    static private final EagerSingletonLogger instance = new EagerSingletonLogger();

    private EagerSingletonLogger() {
        // Private constructor to prevent instantiation
        System.out.println("EagerSingletonLogger instance created");
    }

    public static EagerSingletonLogger getInstance() {
        return instance;
    }

    public void log(String message) {
        System.out.println("Logging message: " + message);
    }
}
