package ratelimit.strategy;

import java.time.Duration;
import java.time.Instant;

import ratelimit.model.RateLimitDecision;

public class FixedWindow implements RateLimitStrategy {
    private final int limit;
    private final Duration window;
    private Instant windowStart;
    private int currentTokens;

    public FixedWindow(int limit, Duration window, Instant start) {
        this.limit = limit;
        this.window = window;
        this.windowStart = start;
        this.currentTokens = limit;
    }

    @Override
    public synchronized RateLimitDecision tryAcquire(Instant reqTime) {
        Instant windowEndTime = windowStart.plus(window);
        // Snap to the current aligned window — do not set windowStart = reqTime.
        // Example: 60s window from T=0, request at T=90s belongs in [60, 120), not [90, 150).
        if (!reqTime.isBefore(windowEndTime)) {
            long windowsPassed = Duration.between(windowStart, reqTime).dividedBy(window);
            windowStart = windowStart.plus(window.multipliedBy(windowsPassed));
            windowEndTime = windowStart.plus(window);
            currentTokens = limit;
        }
        if (currentTokens > 0) {
            currentTokens--;
            // retryAt is only for 429. Allowed requests have nothing to wait for.
            return new RateLimitDecision(this.currentTokens, true, null);
        }
        return new RateLimitDecision(this.currentTokens, false, windowEndTime);
    }
}
