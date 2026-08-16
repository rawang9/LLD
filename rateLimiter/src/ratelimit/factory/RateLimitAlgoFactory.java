package ratelimit.factory;

import ratelimit.clock.Clock;
import ratelimit.model.RateLimitConfig;
import ratelimit.strategy.FixedWindow;
import ratelimit.strategy.LeakyBucket;
import ratelimit.strategy.RateLimitStrategy;
import ratelimit.strategy.SlidingWindowCounter;
import ratelimit.strategy.SlidingWindowLog;
import ratelimit.strategy.TokenBucket;

public class RateLimitAlgoFactory {
    // Instant is passed in — Factory does not call Instant.now(). Facade + Clock will supply this.
    public static RateLimitStrategy create(RateLimitConfig config, Clock startTime) {
        switch (config.getType()) {
            case FIXED_WINDOW: // enum switch labels must be unqualified
                return new FixedWindow(config.getLimit(), config.getWindow(), startTime.now());
            case SLIDING_WINDOW_LOG:
                return new SlidingWindowLog(config.getLimit(), config.getWindow());
            case SLIDING_WINDOW_COUNTER:
                return new SlidingWindowCounter(config.getLimit(), config.getWindow(),startTime.now());
            case TOKEN_BUCKET:
                return new TokenBucket(config.getLimit(), config.getWindow(),startTime.now(), config.getRefillRate());
            case LEAKY_BUCKET:
                return new LeakyBucket(config.getLimit(), config.getWindow(), startTime.now(), config.getRefillRate());
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + config.getType());
        }
    }
}
