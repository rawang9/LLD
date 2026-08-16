package ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import ratelimit.clock.Clock;
import ratelimit.factory.RateLimitAlgoFactory;
import ratelimit.model.RateLimitConfig;
import ratelimit.model.RateLimitDecision;
import ratelimit.strategy.RateLimitStrategy;

public class RateLimiter {
    private final Map<String, RateLimitStrategy> userAlgoMapper;
    private final Clock clock;
    private final Function<String, RateLimitConfig> configOf;

    public RateLimiter(Clock clock, Function<String, RateLimitConfig> configOf) {
        this.clock = clock;
        this.configOf = configOf;
        this.userAlgoMapper = new ConcurrentHashMap<>();
    }
    public RateLimitDecision allow(String key) {
        // computeIfAbsent is atomic per key: two first requests cannot create two strategies (double quota).
        RateLimitStrategy algo = userAlgoMapper.computeIfAbsent(key, k ->
            RateLimitAlgoFactory.create(configOf.apply(k), clock)
        );
        // Same instance for this key; synchronized inside tryAcquire serializes two threads on one user.
        return algo.tryAcquire(clock.now());
    }
}
