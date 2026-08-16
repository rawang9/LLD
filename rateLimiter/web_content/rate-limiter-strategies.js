/**
 * Browser port of Java src/ratelimit/strategy/* — keep in sync with LLD.
 * Decision shape matches RateLimitDecision(remainingTokens, allowed, retryAt).
 */

const WINDOW_MS = 60_000;

/** Mirrors ratelimit.model.RateLimitDecision */
class RateLimitDecision {
  constructor(remainingTokens, allowed, retryAtMs) {
    this.remainingTokens = remainingTokens;
    this.allowed = allowed;
    this.retryAt = retryAtMs ?? null;
  }
}

/** Mirrors ratelimit.model.AlgoType */
const AlgoType = {
  FIXED_WINDOW: 'FIXED_WINDOW',
  SLIDING_WINDOW_LOG: 'SLIDING_WINDOW_LOG',
  SLIDING_WINDOW_COUNTER: 'SLIDING_WINDOW_COUNTER',
  TOKEN_BUCKET: 'TOKEN_BUCKET',
  LEAKY_BUCKET: 'LEAKY_BUCKET',
};

/** Mirrors Main.getConfig(String key) — limit, windowMs, rate (refill/leak per window) */
function getConfig(key) {
  const windowMs = WINDOW_MS;
  if (key.startsWith('user')) {
    return { limit: 10, windowMs, rate: 10, type: AlgoType.FIXED_WINDOW, tier: 'user' };
  }
  if (key.startsWith('admin')) {
    return { limit: 15, windowMs, rate: 15, type: AlgoType.SLIDING_WINDOW_LOG, tier: 'admin' };
  }
  if (key.startsWith('manager')) {
    return { limit: 12, windowMs, rate: 12, type: AlgoType.SLIDING_WINDOW_COUNTER, tier: 'manager' };
  }
  if (key.startsWith('cto')) {
    return { limit: 10, windowMs, rate: 6, type: AlgoType.TOKEN_BUCKET, tier: 'cto' };
  }
  if (key.startsWith('ceo')) {
    return { limit: 10, windowMs, rate: 6, type: AlgoType.LEAKY_BUCKET, tier: 'ceo' };
  }
  return { limit: 5, windowMs, rate: 5, type: AlgoType.FIXED_WINDOW, tier: 'default' };
}

function createStrategy(config, startMs) {
  switch (config.type) {
    case AlgoType.FIXED_WINDOW:
      return new FixedWindow(config.limit, config.windowMs, startMs);
    case AlgoType.SLIDING_WINDOW_LOG:
      return new SlidingWindowLog(config.limit, config.windowMs);
    case AlgoType.SLIDING_WINDOW_COUNTER:
      return new SlidingWindowCounter(config.limit, config.windowMs, startMs);
    case AlgoType.TOKEN_BUCKET:
      return new TokenBucket(config.limit, config.windowMs, startMs, config.rate);
    case AlgoType.LEAKY_BUCKET:
      return new LeakyBucket(config.limit, config.windowMs, startMs, config.rate);
    default:
      return new FixedWindow(config.limit, config.windowMs, startMs);
  }
}

/** Mirrors FixedWindow.java */
class FixedWindow {
  constructor(limit, windowMs, startMs) {
    this.limit = limit;
    this.windowMs = windowMs;
    this.windowStart = startMs;
    this.currentTokens = limit;
  }

  tryAcquire(reqMs) {
    let windowEnd = this.windowStart + this.windowMs;
    // Snap to aligned window — do not set windowStart = reqMs.
    if (reqMs >= windowEnd) {
      const windowsPassed = Math.floor((reqMs - this.windowStart) / this.windowMs);
      this.windowStart += windowsPassed * this.windowMs;
      windowEnd = this.windowStart + this.windowMs;
      this.currentTokens = this.limit;
    }
    if (this.currentTokens > 0) {
      this.currentTokens -= 1;
      return new RateLimitDecision(this.currentTokens, true, null);
    }
    return new RateLimitDecision(this.currentTokens, false, windowEnd);
  }

  vizState(reqMs) {
    let windowStart = this.windowStart;
    let remaining = this.currentTokens;
    const windowEnd = windowStart + this.windowMs;
    if (reqMs >= windowEnd) {
      const windowsPassed = Math.floor((reqMs - windowStart) / this.windowMs);
      windowStart += windowsPassed * this.windowMs;
      remaining = this.limit;
    }
    return {
      windowStart,
      windowEnd: windowStart + this.windowMs,
      remaining,
      limit: this.limit,
      used: this.limit - remaining,
    };
  }
}

/** Mirrors SlidingWindowLog.java — ArrayDeque FIFO */
class SlidingWindowLog {
  constructor(limit, windowMs) {
    this.limit = limit;
    this.windowMs = windowMs;
    this.requestLog = [];
  }

  tryAcquire(reqMs) {
    if (this.limit <= 0) {
      return new RateLimitDecision(0, false, null);
    }

    // Window is (reqTime - W, reqTime]. Stamp exactly W ago is expired.
    const windowStart = reqMs - this.windowMs;
    while (this.requestLog.length > 0) {
      const oldest = this.requestLog[0];
      if (oldest <= windowStart) {
        this.requestLog.shift();
      } else {
        break;
      }
    }

    if (this.requestLog.length < this.limit) {
      this.requestLog.push(reqMs);
      const remaining = this.limit - this.requestLog.length;
      return new RateLimitDecision(remaining, true, null);
    }

    const retryAt = this.requestLog[0] + this.windowMs;
    return new RateLimitDecision(0, false, retryAt);
  }

  vizState(reqMs) {
    const windowStart = reqMs - this.windowMs;
    const timestamps = this.requestLog.filter((t) => t > windowStart);
    return {
      windowStart,
      windowEnd: reqMs,
      remaining: this.limit - timestamps.length,
      limit: this.limit,
      used: timestamps.length,
      timestamps,
    };
  }
}

/** Mirrors SlidingWindowCounter.java */
class SlidingWindowCounter {
  constructor(limit, windowMs, startMs) {
    this.limit = limit;
    this.windowMs = windowMs;
    this.currentStart = startMs;
    this.currentCount = 0;
    this.prevCount = 0;
  }

  tryAcquire(reqMs) {
    let currentEnd = this.currentStart + this.windowMs;
    if (reqMs >= currentEnd) {
      const windowsPassed = Math.floor((reqMs - this.currentStart) / this.windowMs);
      if (windowsPassed === 1) {
        this.prevCount = this.currentCount;
      } else {
        this.prevCount = 0;
      }
      this.currentStart += windowsPassed * this.windowMs;
      this.currentCount = 0;
      currentEnd = this.currentStart + this.windowMs;
    }

    const elapsed = reqMs - this.currentStart;
    const overlap = this.windowMs - elapsed;
    const weightedPrev = Math.floor((overlap * this.prevCount) / this.windowMs);
    const estimated = weightedPrev + this.currentCount;

    if (estimated < this.limit) {
      this.currentCount += 1;
      return new RateLimitDecision(this.limit - estimated - 1, true, null);
    }
    return new RateLimitDecision(0, false, currentEnd);
  }

  vizState(reqMs) {
    let currentStart = this.currentStart;
    let currentCount = this.currentCount;
    let prevCount = this.prevCount;
    let currentEnd = currentStart + this.windowMs;

    if (reqMs >= currentEnd) {
      const windowsPassed = Math.floor((reqMs - currentStart) / this.windowMs);
      prevCount = windowsPassed === 1 ? currentCount : 0;
      currentStart += windowsPassed * this.windowMs;
      currentCount = 0;
      currentEnd = currentStart + this.windowMs;
    }

    const elapsed = reqMs - currentStart;
    const overlap = this.windowMs - elapsed;
    const weightedPrev = Math.floor((overlap * prevCount) / this.windowMs);
    const estimated = weightedPrev + currentCount;

    return {
      currentStart,
      currentEnd,
      prevStart: currentStart - this.windowMs,
      prevEnd: currentStart,
      currentCount,
      prevCount,
      weightedPrev,
      estimated,
      remaining: Math.max(0, this.limit - estimated),
      limit: this.limit,
      overlapPct: Math.round((overlap / this.windowMs) * 100),
    };
  }
}

/** Mirrors TokenBucket.java — starts FULL */
class TokenBucket {
  constructor(limit, windowMs, startMs, refillRate) {
    if (refillRate <= 0) throw new Error('refillRate must be > 0');
    this.bucketCapacity = limit;
    this.msPerToken = windowMs / refillRate;
    if (this.msPerToken <= 0) throw new Error('window too small for refillRate');
    this.lastRefill = startMs;
    this.currentTokens = limit;
  }

  tryAcquire(reqMs) {
    const elapsed = reqMs - this.lastRefill;
    if (elapsed > 0) {
      const produced = Math.floor(elapsed / this.msPerToken);
      if (produced > 0) {
        this.currentTokens = Math.min(this.bucketCapacity, this.currentTokens + produced);
        this.lastRefill += produced * this.msPerToken;
      }
    }
    if (this.currentTokens > 0) {
      this.currentTokens -= 1;
      return new RateLimitDecision(this.currentTokens, true, null);
    }
    return new RateLimitDecision(0, false, this.lastRefill + this.msPerToken);
  }

  snapshot(reqMs) {
    let tokens = this.currentTokens;
    const elapsed = reqMs - this.lastRefill;
    if (elapsed > 0) {
      const produced = Math.floor(elapsed / this.msPerToken);
      if (produced > 0) {
        tokens = Math.min(this.bucketCapacity, tokens + produced);
      }
    }
    return { level: tokens / this.bucketCapacity, count: tokens, capacity: this.bucketCapacity };
  }
}

/** Mirrors LeakyBucket.java — starts EMPTY */
class LeakyBucket {
  constructor(limit, windowMs, startMs, leakRate) {
    if (leakRate <= 0) throw new Error('leakRate must be > 0');
    this.capacity = limit;
    this.msPerLeak = windowMs / leakRate;
    if (this.msPerLeak <= 0) throw new Error('window too small for leakRate');
    this.lastLeak = startMs;
    this.water = 0;
  }

  tryAcquire(reqMs) {
    const elapsed = reqMs - this.lastLeak;
    if (elapsed > 0) {
      const leaked = Math.floor(elapsed / this.msPerLeak);
      if (leaked > 0) {
        this.water = Math.max(0, this.water - leaked);
        this.lastLeak += leaked * this.msPerLeak;
      }
    }
    if (this.water < this.capacity) {
      this.water += 1;
      return new RateLimitDecision(this.capacity - this.water, true, null);
    }
    return new RateLimitDecision(0, false, this.lastLeak + this.msPerLeak);
  }

  snapshot(reqMs) {
    let water = this.water;
    const elapsed = reqMs - this.lastLeak;
    if (elapsed > 0) {
      const leaked = Math.floor(elapsed / this.msPerLeak);
      if (leaked > 0) {
        water = Math.max(0, water - leaked);
      }
    }
    return { level: water / this.capacity, count: water, capacity: this.capacity };
  }
}
