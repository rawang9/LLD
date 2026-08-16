package ratelimit.strategy;

import java.time.Duration;
import java.time.Instant;

import ratelimit.model.RateLimitDecision;

public class TokenBucket implements RateLimitStrategy {
    private final int bucketCapacity;
    private final long nanosPerToken; // window / refillRate: time to earn one token
    private Instant lastRefill;
    private int currentTokens;

    public TokenBucket(int limit, Duration window, Instant start, int refillRate) {
        if (refillRate <= 0) {
            throw new IllegalArgumentException("refillRate must be > 0");
        }
        this.bucketCapacity = limit;
        this.nanosPerToken = window.toNanos() / refillRate;
        if (this.nanosPerToken <= 0) {
            throw new IllegalArgumentException("window too small for refillRate");
        }
        this.lastRefill = start;
        this.currentTokens = limit; // start full — burst = capacity
    }

    @Override
    public synchronized RateLimitDecision tryAcquire(Instant reqTime) {
        long elapsed = Duration.between(lastRefill, reqTime).toNanos();
        if (elapsed > 0) {
            long produced = elapsed / nanosPerToken;
            if (produced > 0) {
                currentTokens = Math.min(bucketCapacity, currentTokens + (int) produced);
                // Advance only by whole tokens so leftover nanos carry to the next call.
                // lastRefill = reqTime would drop e.g. 15s → 1 token and lose 5s (6/min = 1 per 10s).
                lastRefill = lastRefill.plusNanos(produced * nanosPerToken);
            }
        }

        if (currentTokens > 0) {
            currentTokens--;
            return new RateLimitDecision(currentTokens, true, null);
        }
        // Next token is due at lastRefill + nanosPerToken, not reqTime + nanosPerToken
        // (leftover time already counted toward the next token).
        return new RateLimitDecision(0, false, lastRefill.plusNanos(nanosPerToken));
    }
}
