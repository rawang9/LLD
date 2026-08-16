package ratelimit;

import java.time.Duration;

import ratelimit.clock.FakeClock;
import ratelimit.model.AlgoType;
import ratelimit.model.RateLimitConfig;
import ratelimit.model.RateLimitDecision;

public class Main {
    // static so main() can pass it as Main::getConfig (instance methods need an object)
    public static RateLimitConfig getConfig(String key) {
        Duration window = Duration.ofMinutes(1);
        if (key.startsWith("user")) {
            return new RateLimitConfig(10, window, 10, AlgoType.FIXED_WINDOW);
        } else if (key.startsWith("admin")) {
            return new RateLimitConfig(15, window, 15, AlgoType.SLIDING_WINDOW_LOG);
        }
        else if (key.startsWith("manager")) {
            return new RateLimitConfig(12, window, 12, AlgoType.SLIDING_WINDOW_COUNTER);
        }
        else if (key.startsWith("cto")) {
            return new RateLimitConfig(10, window, 6, AlgoType.TOKEN_BUCKET);
        }
        else if (key.startsWith("ceo")) {
            return new RateLimitConfig(10, window, 6, AlgoType.LEAKY_BUCKET);
        }
        return new RateLimitConfig(5, window, 5, AlgoType.FIXED_WINDOW);
    }

    public static void main(String args[]) {
        System.out.println("Rate Limiting testing");
        FakeClock customClock = new FakeClock("2026-08-15T12:00:00Z");
        RateLimiter rateLimiterDoor = new RateLimiter(customClock, Main::getConfig);
        // admin = sliding log, limit 15 → 15 allow, 5 deny; +1 min evicts the burst.
        for (int i = 0; i < 15; i++) {
            RateLimitDecision d = rateLimiterDoor.allow("ceo:123");
            System.out.println(i + " " + d);
        }
        customClock.addMinutes(1);
        RateLimitDecision rolled = rateLimiterDoor.allow("ceo:123");
        System.out.println("after window allowed= " + rolled);
    }
}
