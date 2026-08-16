# Rate Limiter (LLD)

In-memory rate limiter in Java — Facade, Factory, Strategy, five algorithms — plus Redis/Lua for distributed limits.

## Start here

| Doc | What it is |
|-----|------------|
| **[doc/rate-limiter-playground.html](doc/rate-limiter-playground.html)** | **All-in-one page** — live demos for all 5 algos, HLD + LLD at the bottom |
| **[doc/HOW_WE_BUILT_IT.md](doc/HOW_WE_BUILT_IT.md)** | Write-up — request path, text UML, patterns, learnings |
| **[doc/index.html](doc/index.html)** | Same playground (redirect) — use as GitHub Pages entry |

Open the playground locally:

```bash
cd rateLimiter/doc
python3 -m http.server 8080
# → http://localhost:8080/rate-limiter-playground.html
```

(`file://` works for most of the page; the LLD section loads a small embed fragment via `fetch` and needs HTTP.)

## Playground features

- **Lab clock** at `2026-08-15T12:00:00Z` with **1× / 2× / 3×** speed
- **Fixed Window** — 3 tray belt, fixed refill pipe, window roll every 60s lab time
- **Sliding Window Log** — FIFO queue grid with insert timestamps
- **Sliding Window Counter** — dual bars + rollover animation
- **Token / Leaky Bucket** — jar visuals with refill/leak
- **HLD** — `rate-limiter-hld.svg` embedded at end of page
- **LLD** — UML diagram, tables, request path (from `rate-limiter-lld-embed.html`)

JS strategies in `doc/rate-limiter-strategies.js` mirror `src/ratelimit/strategy/*.java`.

## Diagrams (SVG)

| File | What it shows |
|------|----------------|
| [doc/rate-limiter-hld.svg](doc/rate-limiter-hld.svg) | HLD — Gateway, Zookeeper, Redis ×20 |
| [doc/rate-limiter-lld-uml.svg](doc/rate-limiter-lld-uml.svg) | LLD class diagram (standalone) |
| [doc/rate-limiter-strategies-animated.svg](doc/rate-limiter-strategies-animated.svg) | 5 strategies carousel |

## Java quick start

```bash
cd rateLimiter
mkdir -p out
javac -d out $(find src -name '*.java')
java -cp out ratelimit.Main

# Optional: local Redis for Lua scripts
cd infra/redis
./redis-pod.sh start
redis-cli -p 6380 PING
```

## Layout

```
rateLimiter/
  README.md
  src/ratelimit/           Java: Facade, strategies, factory, clock
  infra/redis/             Podman Redis + lua/*.lua
  doc/
    rate-limiter-playground.html   main UI (demos + HLD + LLD)
    rate-limiter-playground.js     visuals + lab clock
    rate-limiter-strategies.js     algo logic (Java port)
    rate-limiter-lld-embed.html    LLD section fragment
    rate-limiter-hld.svg
    HOW_WE_BUILT_IT.md
    index.html                     redirect → playground
```

## Request path (one line)

`allow(key)` → `configOf(key)` → `computeIfAbsent` → `tryAcquire(clock.now())` → `RateLimitDecision`

---

## Host on GitHub Pages

Yes — static HTML/CSS/JS/SVG works on GitHub Pages. This repo publishes **`rateLimiter/doc/`** only (not the whole LLD monorepo).

### One-time setup

1. **Push** this repo to GitHub (branch `main`).
2. Open the repo on GitHub → **Settings** → **Pages**.
3. Under **Build and deployment**:
   - **Source:** GitHub Actions (not “Deploy from branch”).
4. Push to `main` (or run the workflow manually under **Actions** → **Rate limiter docs** → **Run workflow**).
5. After the workflow succeeds, Pages shows a URL like:
   `https://<username>.github.io/<repo-name>/`
   That URL serves `rateLimiter/doc/index.html` → playground.

The workflow file is [`.github/workflows/rate-limiter-pages.yml`](../.github/workflows/rate-limiter-pages.yml).

### Custom domain (optional)

Settings → Pages → **Custom domain** → add your domain and DNS records.

### Troubleshooting

| Issue | Fix |
|-------|-----|
| LLD section says “Could not load” | Pages must serve over HTTPS; embed fetch needs the deployed site, not raw file open |
| 404 on playground | Confirm workflow ran and Pages source is **GitHub Actions** |
| Wrong folder published | Workflow `path` must stay `rateLimiter/doc` |
