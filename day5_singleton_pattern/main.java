package day5_singleton_pattern;

class Main {
    public static void main(String[] args) {
        EnumSingleton enumSingleton = EnumSingleton.INSTANCE;
        enumSingleton.log("Hello, world! from enum");
        ThreadSafeSingleton threadSafeSingleton = ThreadSafeSingleton.getInstance();
        threadSafeSingleton.log("Hello, world!");
        TwoWayThreadSafe twoWayThreadSafe = TwoWayThreadSafe.getInstance();
        twoWayThreadSafe.log("Hello, world!");
        LazySingletonLogger lazySingletonLogger = LazySingletonLogger.getInstance();
        lazySingletonLogger.log("Hello, world!");
        EagerSingletonLogger eagerSingletonLogger = EagerSingletonLogger.getInstance();
        eagerSingletonLogger.log("Hello, world!");
    }
}
