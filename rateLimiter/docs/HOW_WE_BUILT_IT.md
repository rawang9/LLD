# Rate Limiter LLD — How it works, how we built it, what we learned

Companion to [`../README.md`](../README.md). **Live demo:** [`rate-limiter-playground.html`](../web_content/rate-limiter-playground.html) (five algos + HLD/LLD on one page).

---

## What problem this solves

An API must answer: **is this key allowed to make one more request right now?**

If yes → handle the request.  
If no → HTTP 429 + remaining + when to retry.

A rate limiter is a **doorman**. Algorithms are just different clickers.

```
Request
   |
   v
RateLimiter.allow(key)          ← Facade (the only API callers need)
   |
   +-- configOf.apply(key)      ← guest list (user vs admin vs cto)
   +-- Factory.create(config)   ← first time only (computeIfAbsent)
   |
   v
strategy.tryAcquire(clock.now())
   |
   v
RateLimitDecision { allowed, remaining, retryAt }
```

---

## Interactive playground (what we shipped)

Single page: **`rate-limiter-playground.html`**

| Section | Visual | JS mirrors |
|---------|--------|------------|
| Fixed Window | 3-tray belt, fixed refill pipe, 60s roll | `FixedWindow.java` |
| Sliding Log | 5×3 queue grid, timestamps | `SlidingWindowLog.java` |
| Sliding Counter | prev/current bars, rollover | `SlidingWindowCounter.java` |
| Token Bucket | jar + tokens | `TokenBucket.java` |
| Leaky Bucket | bucket + drip | `LeakyBucket.java` |

- **Lab clock** — frozen start `2026-08-15T12:00:00Z`, runs at 1× / 2× / 3×
- **Strategies** — `rate-limiter-strategies.js` (same decisions as Java)
- **HLD** — `rate-limiter-hld.svg` at bottom of page
- **LLD** — UML + tables loaded from `rate-limiter-lld-embed.html`

Run locally: `cd doc && python3 -m http.server 8080`

---

## UML class diagram

Facade + Factory + Strategy + DI. Draw **`RateLimiter`** (the doorman) first. Tier rules stay in `configOf`, not on this diagram.

```
+---------------------------- DI -----------------------------+
|  <<interface>> Clock                                        |
|  + now(): Instant                                           |
|  FakeClock | SystemClock                                    |
+----------------------------+--------------------------------+
                             | uses
                             v
+------------------------ FACADE -----------------------------+     reads      +------------------+
|  <<Facade + Strategy Context>>                              |--------------->| RateLimitConfig  |
|  RateLimiter                                                |                |  <<record>>      |
|  - byKey: Map<String, RateLimitStrategy>   (1 per key *)    |                |  limit, window   |
|  - clock: Clock                                             |                |  refillRate      |
|  - configOf: Function<String, Config>                       |                |  type: AlgoType  |
|  + allow(key): RateLimitDecision                            |                +--------+---------+
+-------|------------------------------|----------------------+                         | type
        | returns                      | uses                                          v
        v                                v                                  +-----------+-----------+
+------------------+          +---------+----------+                       | AlgoType <<enum>>     |
| RateLimitDecision|          | RateLimitAlgoFactory|                       | FIXED_WINDOW          |
|  <<record>>      |          |  <<Factory>>        |                       | SLIDING_WINDOW_LOG    |
|  allowed         |          |  + create(config,   |                       | SLIDING_WINDOW_COUNTER|
|  remaining       |          |      clock): Algo   |                       | TOKEN_BUCKET          |
|  retryAt         |          +---------+----------+                       | LEAKY_BUCKET          |
+------------------+                    | <<create>> (dashed)               +-----------------------+
                                        v
+---------------------------- STRATEGY -------------------------------------+
|  <<interface>> RateLimitStrategy                                            |
|  + tryAcquire(now: Instant): RateLimitDecision                              |
+-------^-------------^------------------^-------------^-------------^--------+
        |             |                  |             |             |
   +----+----+   +----+----+        +----+----+   +----+----+   +----+----+
   | Fixed   |   | Sliding |        | Sliding |   | Token   |   | Leaky   |
   | Window  |   | Window  |        | Window  |   | Bucket  |   | Bucket  |
   |         |   | Log     |        | Counter |   |         |   |         |
   +---------+   +---------+        +---------+   +---------+   +---------+

Legend (say while drawing):
  RateLimiter --◆--> RateLimitStrategy   composition (one algo instance per key)
  Factory - - - -> Algo                    dashed = create on first allow(key)
  Concrete strategies --△--> interface     hollow triangle = implements
  Clock, configOf injected               DI (no Singleton on factory)
```

Full SVG UML is in the playground LLD section and in [`rate-limiter-lld-uml.svg`](./rate-limiter-lld-uml.svg).

---

## How we built it (bottom-up)

We did **not** start with Redis. Interviewers want a correct in-memory design first.

| Step | What we added | Why that order |
|---|---|---|
| 1 | **Strategy** — `RateLimitStrategy.tryAcquire(Instant)` + Fixed Window | One interface, one algo, `(limit, window)` not `Tier` |
| 2 | **Factory** — `create(config, clock)` + `AlgoType` enum | No singleton, no `"tb"` magic strings |
| 3 | **DI Clock** — `Clock` / `FakeClock` / `SystemClock` | Tests freeze time; no `LocalTime` midnight wrap |
| 4 | **Facade** — `RateLimiter.allow(key)` | The request path. Map + `computeIfAbsent` |
| 5 | **configOf** — `Function<String, RateLimitConfig>` | Product rules (Gold/Admin) stay outside the doorman |
| 6 | More strategies | Sliding log, sliding counter, token bucket, leaky bucket |
| 7 | Packages under `src/ratelimit/` | Same logic, one class per file |
| 8 | **Playground** | JS port + visuals so algos are tangible before Redis |
| 9 | Redis pod (Podman) | Next: Lua so refill+consume is atomic across machines |

`Main` only wires Clock + `Main::getConfig` and calls `allow`. It does not own algorithms.

---

## Design patterns (what to say while drawing UML)

| Pattern | Where | Job |
|---|---|---|
| **Facade** | `RateLimiter` | One method: `allow(key)` |
| **Strategy Context** | same class | Holds one algo per key in `ConcurrentHashMap` |
| **Factory** | `RateLimitAlgoFactory` | `new` the right algorithm from config |
| **Strategy** | `RateLimitStrategy` + 5 classes | Swap counting math without changing the facade |
| **DI** | `Clock`, `configOf` | Time and guest list are injected, not hardcoded |

**Not a Singleton.** The factory is stateless. The app creates **one** `RateLimiter` at startup and passes it in. `getInstance()` hurts tests (FakeClock) and blocks a second limiter (per-IP vs per-user).

---

## Request path (Java)

```java
public RateLimitDecision allow(String key) {
    RateLimitStrategy algo = userAlgoMapper.computeIfAbsent(key, k ->
        RateLimitAlgoFactory.create(configOf.apply(k), clock)
    );
    return algo.tryAcquire(clock.now());
}
```

- `configOf.apply(k)` — Java for “call this function” (Python: `config_of(k)`).
- `this::createFor` vs `createFor` — the map wants a **function object**, not a method name. `this::createFor` means “the createFor of this instance.”
- `computeIfAbsent` is atomic **per key**: two first requests cannot create two strategies (double quota).
- `synchronized` on `tryAcquire` serializes two threads on the **same** user.

---

## Algorithms (20 seconds each)

| Algo | Idea | Tradeoff |
|---|---|---|
| **Fixed Window** | Reset count every T; snap start to aligned windows | Cheap. **2× burst** at the boundary (10 at 10:00:59 + 10 at 10:01:00). |
| **Sliding Log** | Store every timestamp; drop older than T | Exact. Memory **O(limit)**. |
| **Sliding Counter** | Weighted previous window + current | Approximate. **O(1)** memory. Weight = overlap of prev with last T. |
| **Token Bucket** | Tokens refill; request spends 1 | Burst = capacity. Sustained = refillRate / window. Start **full**. |
| **Leaky Bucket** | Water queues; hole leaks at constant rate | Burst is **smoothed**. Start **empty**. Request **adds** water. |

Token vs leaky (do not mix):

```
TOKEN BUCKET                         LEAKY BUCKET
jar of tickets                       bucket of water with a hole
time ADDS tokens                     time REMOVES water
request SPENDS a ticket              request ADDS water
empty → deny                         full → deny
burst allowed                        output is a drip
```

---

## Learnings (mistakes we actually made)

### LLD, not a catalog

Five algorithms with no `allow(key)` is a mid-level sketch. The interviewer grades the **request path**.

### Time

`LocalTime` is “3:30 PM” — no date. `toSecondOfDay()` goes negative at midnight. Use **`Instant` + injected `Clock`**. Tests call `addMinutes(1)` instead of `Thread.sleep`.

### Remainder

If you integer-divide elapsed time and then set `lastRefill = reqTime`, leftover nanos are gone. Refill/leak silently slows down.

Keep leftover:

```text
lastRefill += produced * nanosPerToken
```

not `lastRefill = reqTime`.

### Config vs Tier

Algorithms take **numbers** `(limit, window, refillRate)`. “Gold / Admin / CTO” lives in `getConfig` (the guest list), not inside `TokenBucket`.

### Windows stay on a grid

`currentStart = reqTime` on rollover mis-aligns the previous count from the weight formula. Snap:

```text
currentStart += window * windowsPassed
```

A request at 12:01:30 belongs in `[12:01, 12:02)`.

### Java Function

`Function<String, RateLimitConfig> configOf` is a function object. Call it with `.apply(key)`. Pass it with `Main::getConfig` (static method reference).

### Concurrency

The race is **who owns the instance for a key**, not only `synchronized` inside `tryAcquire`. Use `computeIfAbsent`.

### Naming

`LeakyTokenBucket` mixed two algorithms. Water is not tokens. `retryAt` is an `Instant` (when), not a Duration, unless you document the conversion.

### Singleton

Use it only if one instance **must** exist **and** hold shared mutable state. A factory that only `new`s objects does not qualify. One injected `RateLimiter` is enough for the map.

### Playground ↔ Java

Keep `rate-limiter-strategies.js` aligned with Java strategy classes. Visuals are presentation; decisions must match `tryAcquire` for the demo to teach the LLD.

---

## Folder layout

```
rateLimiter/
  README.md
  src/ratelimit/              Java implementation
  infra/redis/                Podman + Lua scripts
  doc/
    rate-limiter-playground.html    demos + HLD + LLD (main entry)
    rate-limiter-playground.js      UI + belt/queue visuals
    rate-limiter-strategies.js      algo logic (Java port)
    rate-limiter-lld-embed.html     LLD section (UML, tables)
    rate-limiter-hld.svg
    HOW_WE_BUILT_IT.md              this file
    index.html                      redirect → playground
```

In-memory limiter = one JVM. Two app servers = two maps = double quota. Redis + Lua is the distributed follow-up: one `EVAL` so GET/refill/SET cannot interleave.

---

## Host on GitHub Pages

See **[README — Host on GitHub Pages](../README.md#host-on-github-pages)**. Summary:

1. Push to GitHub (`main`).
2. Settings → Pages → Source: **GitHub Actions**.
3. Workflow `.github/workflows/rate-limiter-pages.yml` publishes all of `rateLimiter/` (`web_content/` + `docs/`).
4. Open `https://<user>.github.io/<repo>/` → redirects into the playground.

---

## Room script

1. Clarify key (user / IP / user+API), limits, in-memory vs Redis, return remaining?
2. Draw the UML (doorman first).
3. Implement one algo well; others behind the same interface.
4. Say unprompted: 2× burst, remainder, `computeIfAbsent`, Redis only if asked.

**Memorize:** Doorman first. Numbers not Tier. Instant not LocalTime. Keep remainder. Decision not boolean. Enum factory, no singleton. `computeIfAbsent`. Say 2× burst.
