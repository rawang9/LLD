package day5_singleton_pattern;

enum EnumSingleton {
    INSTANCE;

    public void log(String message) {
        System.out.println("Logging message: " + message);
    }
}