package ratelimit.clock;

import java.time.Instant;

public class SystemClock implements Clock {
    public Instant now() { return Instant.now(); }
}
