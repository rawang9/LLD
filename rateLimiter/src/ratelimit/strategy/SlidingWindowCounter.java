package ratelimit.strategy;

import java.time.Duration;
import java.time.Instant;

import ratelimit.model.RateLimitDecision;

public class SlidingWindowCounter implements RateLimitStrategy {
    private final int limit;
    private final Duration window;
    private int currentCount;
    private Instant currentStart;
    private int prevCount;

    public SlidingWindowCounter(int limit, Duration window, Instant start) {
        this.limit = limit;
        this.window = window;
        this.currentStart = start;
        this.currentCount = 0;
        this.prevCount = 0;
    }

    @Override
    public synchronized RateLimitDecision tryAcquire(Instant reqTime) {
        Instant currentEnd = currentStart.plus(window);
        // Snap to aligned windows — do not set currentStart = reqTime (same bug as early FixedWindow).
        // Request at 12:01:30 with a 1min window belongs in [12:01, 12:02), not a new window at 12:01:30.
        if (!reqTime.isBefore(currentEnd)) {
            long windowsPassed = Duration.between(currentStart, reqTime).dividedBy(window);
            if (windowsPassed == 1) {
                prevCount = currentCount; // adjacent window: old current becomes previous
            } else {
                prevCount = 0; // skipped ≥2 windows; the gap had no traffic
            }
            currentStart = currentStart.plus(window.multipliedBy(windowsPassed));
            currentCount = 0;
            currentEnd = currentStart.plus(window);
        }

        // Sliding view [now - W, now] still covers `overlap` of the previous fixed window.
        // At elapsed=0, weight=100% of prev. At elapsed=W, weight=0.
        long windowNanos = window.toNanos();
        long elapsed = Duration.between(currentStart, reqTime).toNanos();
        long overlap = windowNanos - elapsed;
        int weightedPrev = (int) ((overlap * (long) prevCount) / windowNanos);
        int estimated = weightedPrev + currentCount;

        if (estimated < limit) {
            currentCount++;
            // estimated is before this request; remaining after taking one is limit - estimated - 1
            return new RateLimitDecision(limit - estimated - 1, true, null);
        }
        // Approximate retry: when this fixed window ends, previous weight drops to 0.
        return new RateLimitDecision(0, false, currentEnd);
    }
}
