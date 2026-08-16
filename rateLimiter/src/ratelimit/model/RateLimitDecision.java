package ratelimit.model;

import java.time.Instant;

public class RateLimitDecision {
    final int remainingTokens;
    final boolean allowed;   // maps to HTTP 200 vs 429
    final Instant retryAt;   // Instant, not Duration: caller does retryAt - now → Retry-After seconds
    public RateLimitDecision(int remainingTokens, boolean allowed, Instant retryAt) {
        this.remainingTokens = remainingTokens;
        this.allowed = allowed;
        this.retryAt = retryAt;
    }
    @Override
    public String toString(){
        String output = " allowed=" + allowed
        + " remaining=" + remainingTokens
        + " retryAt=" + retryAt;
        return output;
    }
}
