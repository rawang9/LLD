package ratelimit.clock;

import java.time.Duration;
import java.time.Instant;

public class FakeClock implements Clock {
    private Instant customClock;
    public FakeClock(String parseTime){
        // parseTime = "20260-08-15T17:30:00Z"
        this.customClock = Instant.parse(parseTime);
    }
    public Instant now(){
        return customClock;
    }
    public void addMinutes(int minutes){
        this.customClock = this.customClock.plus(Duration.ofMinutes(minutes));
    }
    public void addSeconds(int second){
        this.customClock = this.customClock.plus(Duration.ofSeconds(second));
    }
}
