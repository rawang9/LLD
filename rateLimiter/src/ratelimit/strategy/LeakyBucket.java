package ratelimit.strategy;

import java.time.Duration;
import java.time.Instant;

import ratelimit.model.RateLimitDecision;

public class LeakyBucket implements RateLimitStrategy {
    private final int capacity;          // max water = max queued requests
    private final long nanosPerLeak;     // window / leakRate: time for 1 unit to drip out
    private Instant lastLeak;
    private int water;                   // queued work; 0 = empty (allow), capacity = full (deny)

    public LeakyBucket(int limit, Duration window, Instant start, int leakRate) {
        if (leakRate <= 0) {
            throw new IllegalArgumentException("leakRate must be > 0");
        }
        this.capacity = limit;
        this.nanosPerLeak = window.toNanos() / leakRate;
        if (this.nanosPerLeak <= 0) {
            throw new IllegalArgumentException("window too small for leakRate");
        }
        this.lastLeak = start;
        this.water = 0; // start EMPTY — opposite of TokenBucket
    }

    @Override
    public synchronized RateLimitDecision tryAcquire(Instant reqTime) {
        long elapsed = Duration.between(lastLeak, reqTime).toNanos();
        if (elapsed > 0) {
            long leaked = elapsed / nanosPerLeak;
            if (leaked > 0) {
                // Leak even when water is 0 so lastLeak still advances (idle empty must not
                // bank an hour of elapsed and then burst capacity+1).
                water = Math.max(0, water - (int) leaked);
                lastLeak = lastLeak.plusNanos(leaked * nanosPerLeak);
            }
        }

        if (water < capacity) {
            water++;
            return new RateLimitDecision(capacity - water, true, null);
        }
        // Next 1 unit drips at lastLeak + nanosPerLeak (leftover already counted).
        return new RateLimitDecision(0, false, lastLeak.plusNanos(nanosPerLeak));
    }
}
