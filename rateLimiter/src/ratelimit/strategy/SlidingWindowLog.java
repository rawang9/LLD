package ratelimit.strategy;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Queue;

import ratelimit.model.RateLimitDecision;

public class SlidingWindowLog implements RateLimitStrategy {
    private final Duration window;
    private final int limit;
    private final Queue<Instant> requestLog;

    public SlidingWindowLog(int limit, Duration window) {
        this.limit = limit;
        this.window = window;
        this.requestLog = new ArrayDeque<>();
    }

    @Override
    public synchronized RateLimitDecision tryAcquire(Instant reqTime) {
        // Assume reqTime only moves forward. A backward clock would unsort the queue.
        if (limit <= 0) {
            return new RateLimitDecision(0, false, null);
        }

        // Window is (reqTime - W, reqTime]. A stamp exactly W ago is expired.
        Instant windowStart = reqTime.minus(window);
        while (!requestLog.isEmpty()) {
            Instant oldest = requestLog.peek();
            if (oldest.isBefore(windowStart) || oldest.equals(windowStart)) {
                requestLog.poll();
            } else {
                break; // queue is oldest-first; later stamps are newer
            }
        }

        if (requestLog.size() < limit) {
            requestLog.offer(reqTime);
            int remainingTokens = limit - requestLog.size();
            return new RateLimitDecision(remainingTokens, true, null);
        }

        // Oldest in-window stamp drops out at oldest + W — that is the earliest retry.
        Instant retryAt = requestLog.peek().plus(window);
        return new RateLimitDecision(0, false, retryAt);
    }
}
