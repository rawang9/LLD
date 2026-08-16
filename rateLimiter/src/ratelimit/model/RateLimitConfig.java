package ratelimit.model;

import java.time.Duration;

public class RateLimitConfig {
    // private final so nobody mutates limit/window after Factory.create()
    private final int limit;
    private final Duration window;
    private final int refillRate; // unused by FixedWindow; TokenBucket will use it
    private final AlgoType type;

    public RateLimitConfig(int limit, Duration window, int refillRate, AlgoType type) {
        this.limit = limit;
        this.window = window;
        this.refillRate = refillRate;
        this.type = type;
    }

    public int getLimit() { return limit; }
    public Duration getWindow() { return window; }
    public int getRefillRate() { return refillRate; }
    public AlgoType getType() { return type; }
}
