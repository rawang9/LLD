package ratelimit.strategy;

import java.time.Instant;

import ratelimit.model.RateLimitDecision;

public interface RateLimitStrategy {
    RateLimitDecision tryAcquire(Instant reqTime);
}
