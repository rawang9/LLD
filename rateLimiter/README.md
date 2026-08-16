# Rate Limiter (LLD)

In-memory rate limiter in Java — Facade, Factory, Strategy, five algorithms — plus Redis/Lua for distributed limits.

## Start here

| Doc | What it is |
|-----|------------|
| **[web_content/rate-limiter-playground.html](web_content/rate-limiter-playground.html)** | **All-in-one page** — live demos for all 5 algos, HLD + LLD at the bottom |
| **[docs/HOW_WE_BUILT_IT.md](docs/HOW_WE_BUILT_IT.md)** | Write-up — request path, text UML, patterns, learnings |
| **[index.html](index.html)** | Redirect → playground — use as GitHub Pages entry |

Open the playground locally:

```bash
cd rateLimiter/web_content
python3 -m http.server 8080
# → http://localhost:8080/rate-limiter-playground.html
```

Responsive layout — resizes down to phone widths (header nav scrolls horizontally, sections restack) and up to desktop.

(`file://` works for most of the page, including the Notes section, which is now embedded inline; the LLD section still loads a small embed fragment via `fetch` and needs HTTP.)

## Playground features

- **Lab clock** at `2026-08-15T12:00:00Z` with **1× / 2× / 3×** speed
- **Fixed Window** — 3 tray belt, fixed refill pipe, window roll every 60s lab time
- **Sliding Window Log** — FIFO queue grid with insert timestamps
- **Sliding Window Counter** — dual bars + rollover animation
- **Token / Leaky Bucket** — jar visuals with refill/leak
- **HLD** — `rate-limiter-hld.svg` embedded at end of page (from `docs/`)
- **LLD** — UML diagram, tables, request path (from `rate-limiter-lld-embed.html`)

JS strategies in `web_content/rate-limiter-strategies.js` mirror `src/ratelimit/strategy/*.java`.

## Diagrams (SVG)

| File | What it shows |
|------|----------------|
| [docs/rate-limiter-hld.svg](docs/rate-limiter-hld.svg) | HLD — Gateway, Zookeeper, Redis ×20 |
| [docs/rate-limiter-lld-uml.svg](docs/rate-limiter-lld-uml.svg) | LLD class diagram (standalone) |

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
  index.html                       redirect → web_content/rate-limiter-playground.html
  src/ratelimit/                   Java: Facade, strategies, factory, clock
  infra/redis/                     Podman Redis + lua/*.lua
  web_content/                     interactive playground (HTML/CSS/JS)
    rate-limiter-playground.html   main UI (demos + HLD + LLD + notes, embedded)
    rate-limiter-playground.css
    rate-limiter-playground.js     visuals + lab clock
    rate-limiter-strategies.js     algo logic (Java port)
    notes-renderer.js              renders the embedded notes into HTML
    rate-limiter-lld-embed.html    LLD section fragment (fetched at runtime)
    index.html                     redirect → playground
  docs/                            design write-up + standalone diagrams
    HOW_WE_BUILT_IT.md
    rate-limiter-hld.svg
    rate-limiter-lld-uml.svg
```

`web_content/` and `docs/` are separate folders but ship together — the GitHub Actions workflow publishes all of `rateLimiter/`, and the playground links into `docs/` with relative `../docs/...` paths.

## Request path (one line)

`allow(key)` → `configOf(key)` → `computeIfAbsent` → `tryAcquire(clock.now())` → `RateLimitDecision`

---

## Host on GitHub Pages

Yes — static HTML/CSS/JS/SVG works on GitHub Pages. This repo publishes **`rateLimiter/`** (not the whole LLD monorepo) — `index.html`, `web_content/`, and `docs/`; `src/` and `infra/` are just along for the ride as inert files.

### One-time setup

1. **Push** this repo to GitHub (branch `main`).
2. Open the repo on GitHub → **Settings** → **Pages**.
3. Under **Build and deployment**:
   - **Source:** GitHub Actions (not “Deploy from branch”).
4. Push to `main` (or run the workflow manually under **Actions** → **Rate limiter docs** → **Run workflow**).
5. After the workflow succeeds, Pages shows a URL like:
   `https://<username>.github.io/<repo-name>/`
   That URL serves `rateLimiter/index.html`, which redirects into `web_content/rate-limiter-playground.html`.

The workflow file is [`.github/workflows/rate-limiter-pages.yml`](../.github/workflows/rate-limiter-pages.yml).

### Custom domain (optional)

Settings → Pages → **Custom domain** → add your domain and DNS records.

### Troubleshooting

| Issue | Fix |
|-------|-----|
| LLD section says “Could not load” | Pages must serve over HTTPS; embed fetch needs the deployed site, not raw file open |
| 404 on playground | Confirm workflow ran and Pages source is **GitHub Actions** |
| Wrong folder published | Workflow `path` must stay `rateLimiter` |
| HLD image or docs links 404 on live site | Confirm `docs/` shipped alongside `web_content/` in the same artifact — they must be siblings under the published `rateLimiter/` root |
