package day5_singleton_pattern;

public class ThreadSafeSingleton {
    private static ThreadSafeSingleton instance;

    private ThreadSafeSingleton() {
        // Private constructor to prevent instantiation
        System.out.println("ThreadSafeSingleton instance created");
    }

    public static synchronized ThreadSafeSingleton getInstance() {
        if (instance == null) {
            instance = new ThreadSafeSingleton();
        }
        return instance;
    }

    public void log(String message) {
        System.out.println("Logging message: " + message);
    }
}
