/** Playground UI — strategies live in rate-limiter-strategies.js (Java LLD port) */

const LAB_START_ISO = '2026-08-15T12:00:00.000Z';
const LOG_MAX = 12;

function formatTime(ms) {
  return new Date(ms).toISOString().replace('.000', '');
}

function formatRetry(retryAt) {
  if (retryAt == null) return '-';
  const sec = Math.max(0, Math.ceil((retryAt - nowMs()) / 1000));
  return `${formatTime(retryAt)} (${sec}s)`;
}

let labStartMs = Date.parse(LAB_START_ISO);
let pageOpenMs = Date.now();
let labSpeed = 1;

function nowMs() {
  return labStartMs + (Date.now() - pageOpenMs) * labSpeed;
}

function setLabSpeed(speed) {
  const t = nowMs();
  labSpeed = speed;
  labStartMs = t;
  pageOpenMs = Date.now();
  updateSpeedButtons();
  tickClock();
}

function updateSpeedButtons() {
  document.querySelectorAll('[data-speed]').forEach((btn) => {
    const on = Number(btn.dataset.speed) === labSpeed;
    btn.classList.toggle('speed-btn-active', on);
    btn.setAttribute('aria-pressed', on ? 'true' : 'false');
  });
  const hint = document.querySelector('.clock-hint');
  if (hint) {
    hint.textContent = labSpeed === 1
      ? 'Refill, leak, and window rollover use this moving time.'
      : `Lab time running at ${labSpeed}× — refill, leak, and window roll are accelerated.`;
  }
}

const ALGOS = [
  {
    id: 'fixed',
    key: 'user',
    name: 'Fixed Window',
    config: 'limit=10, window=60s',
    lede: 'A set number of requests per fixed time window. Each allowed request decrements a counter; at window start the counter resets to the full limit. Simple and predictable — but bursts near a window boundary can allow up to 2× the limit.',
    pros: ['Simple to implement', 'Predictable for users', 'Low memory (one counter)'],
    cons: ['Boundary burst up to 2× limit', 'Window start time matters'],
    burstHint: 'Fresh window starts with 10 tokens. Burst ×15 → 10 ALLOW, 5 DENY.',
  },
  {
    id: 'log',
    key: 'admin',
    name: 'Sliding Window Log',
    config: 'limit=15, window=60s',
    lede: 'Stores a timestamp for every allowed request in a FIFO queue. Before each decision, expired stamps are polled from the head. Exact and smooth — but memory grows with traffic.',
    pros: ['Exact sliding window', 'Smooth traffic distribution', 'No boundary spike'],
    cons: ['Memory grows with request count', 'Prunes stale entries on every call'],
    burstHint: 'Empty queue. Burst ×20 → 15 ALLOW, 5 DENY (exact limit).',
  },
  {
    id: 'counter',
    key: 'manager',
    name: 'Sliding Window Counter',
    config: 'limit=12, window=60s',
    lede: 'Two fixed-window counters: previous and current. Weight prev by overlap with the sliding view, add current count. When a window rolls, currentCount becomes prevCount.',
    pros: ['Memory-efficient', 'Good for high load', 'Near-exact long-term rate'],
    cons: ['Approximation, not exact', 'Less intuitive than fixed window'],
    burstHint: 'Empty counters. Burst ×17 → 12 ALLOW, 5 DENY (approx algo).',
  },
  {
    id: 'token',
    key: 'cto',
    name: 'Token Bucket',
    config: 'cap=10, refill=6/min, start FULL',
    lede: 'A bucket fills with tokens at a constant rate. Each request spends one token; when empty, requests are denied. Bucket capacity sets max burst; refill rate sets the long-term average.',
    pros: ['Allows controlled bursts', 'Separate burst vs average rate', 'Flexible for APIs'],
    cons: ['Harder to explain limits to users', 'Refill timing less predictable'],
    burstHint: 'Jar starts FULL (burst by design). Burst ×15 → 10 ALLOW, 5 DENY.',
  },
  {
    id: 'leaky',
    key: 'ceo',
    name: 'Leaky Bucket',
    config: 'cap=10, leak=6/min, start EMPTY',
    lede: 'Requests pour water into a bucket that leaks at a fixed rate. If the bucket is full, the request is denied. Output is smoothed to a steady drip.',
    pros: ['Smooth, steady output rate', 'Protects downstream from spikes', 'Queue-like semantics'],
    cons: ['No burst allowance once full', 'Requests wait implicitly via leak'],
    burstHint: 'Bucket starts EMPTY but fills fast. Burst ×15 → 10 ALLOW, 5 DENY (cap).',
  },
].map((def) => {
  const cfg = getConfig(def.key);
  return {
    ...def,
    limit: cfg.limit,
    tier: cfg.tier,
    create: (startMs) => createStrategy(cfg, startMs),
  };
});

function vizLegendHtml() {
  return `
    <div class="viz-legend">
      <span class="leg-item leg-ok"><span class="leg-dot"></span> ALLOW</span>
      <span class="leg-item leg-deny"><span class="leg-dot"></span> DENY</span>
    </div>`;
}

const state = {};

/** Fixed window belt — 3 slots; discrete 60s roll shifts belt left */
const CONVEYOR = {
  viewW: 560,
  viewH: 290,
  trayW: 172,
  beltOriginX: 22,
  trayY: 78,
  trayH: 124,
  beltY: 214,
  pipeTop: 6,
  rollAnimMs: 900,
  traySlots: [-1, 0, 1],
};

function easeOutCubic(t) {
  return 1 - (1 - t) ** 3;
}

/** Fixed X — refill pipe stays here; only trays move on the belt */
function currentPipeX() {
  return trayXAtSlot(1, 0);
}

function windowDriftOffset(now, centerStart) {
  const elapsed = Math.max(0, now - centerStart);
  const phase = Math.min(1, elapsed / WINDOW_MS);
  return -phase * CONVEYOR.trayW;
}

function fixedCenterStart(now) {
  const algo = state.fixed?.algo;
  if (algo?.vizState) return algo.vizState(now).windowStart;
  return Math.floor((now - Date.parse(LAB_START_ISO)) / WINDOW_MS) * WINDOW_MS + Date.parse(LAB_START_ISO);
}

function getFixedBeltOffset(now = nowMs()) {
  const { offset: rollOffset, rolling } = getBeltRollOffset();
  if (rolling) return rollOffset;
  return windowDriftOffset(now, fixedCenterStart(now));
}

function rollAnimDurationMs() {
  return CONVEYOR.rollAnimMs / labSpeed;
}

function traySlotIndex(role) {
  if (role === 'prev') return 0;
  if (role === 'current') return 1;
  if (role === 'next') return 2;
  return 1;
}

function trayXAtSlot(slotIndex, rollOffsetPx = 0) {
  return CONVEYOR.beltOriginX + slotIndex * CONVEYOR.trayW + rollOffsetPx;
}

function getBeltRollOffset() {
  const roll = state.fixed?.beltRoll;
  if (!roll?.active) return { offset: 0, rolling: false };
  const dur = rollAnimDurationMs();
  const t = Math.min(1, (Date.now() - roll.startAt) / dur);
  const offset = -CONVEYOR.trayW * easeOutCubic(t);
  if (t >= 1) {
    roll.active = false;
    state.fixed.beltRoll = null;
    return { offset: 0, rolling: false, justFinished: true };
  }
  return { offset, rolling: true };
}

function triggerFixedBeltRoll(oldCenterStart) {
  const s = state.fixed;
  if (!s || s.beltRoll?.active) return;
  s.beltRoll = { active: true, startAt: Date.now(), oldCenterStart };
  const viz = document.getElementById('viz-fixed');
  const hint = document.getElementById('fixed-roll-hint');
  viz?.classList.add('conveyor-rolling');
  if (hint) {
    hint.textContent = '↻ window roll · prev removed · next → current · new next on belt';
    hint.classList.add('fixed-roll-hint-show');
  }
  setTimeout(() => {
    viz?.classList.remove('conveyor-rolling');
    hint?.classList.remove('fixed-roll-hint-show');
    if (hint) hint.textContent = '';
  }, rollAnimDurationMs() + 400);
}

/** Refill funnel — fixed station; trays pass underneath */
function refillPipeMarkup(topY) {
  return `
    <g class="refill-station">
      <rect x="-22" y="${topY - 2}" width="44" height="32" rx="8" class="refill-station-bg"/>
      <path d="M -7 ${topY + 4} L 7 ${topY + 4} L 3.5 ${topY + 16} L -3.5 ${topY + 16} Z" class="refill-pipe-nozzle"/>
      <line x1="0" y1="${topY + 16}" x2="0" y2="${topY + 26}" class="refill-pipe-drip"/>
      <circle cx="0" cy="${topY + 30}" r="3.5" class="refill-pipe-token"/>
      <text x="0" y="${topY - 4}" text-anchor="middle" class="cv-label refill-label">refill</text>
      <line x1="0" y1="${topY + 34}" x2="0" y2="${CONVEYOR.trayY - 6}" class="refill-pipe-guide"/>
    </g>`;
}

const LOG_VIZ = {
  viewW: 720,
  viewH: 360,
  pipeX: 360,
  trackX: 36,
  trackY: 58,
  trackW: 648,
  trackH: 250,
  cols: 5,
  rows: 3,
  gap: 12,
};

const CONVEYOR_IDS = ['fixed'];
const QUEUE_ID = 'log';
const COUNTER_ID = 'counter';
const VIZ_PANEL_IDS = ['fixed', 'log', 'counter', 'token', 'leaky'];

/** Pending drop animations */
const dropQueues = { fixed: [], log: [], counter: [] };

/** Slim funnel pipe — shared by log viz */
function slimPipeMarkup(cx, topY) {
  return `
    <g class="slim-pipe" transform="translate(${cx}, ${topY})">
      <path d="M -5 0 L 5 0 L 2.5 10 L -2.5 10 Z" class="slim-pipe-nozzle"/>
      <line x1="0" y1="10" x2="0" y2="20" class="slim-pipe-drip"/>
    </g>`;
}

function trayRoleForStart(windowStart, centerStart) {
  const rel = Math.round((windowStart - centerStart) / WINDOW_MS);
  if (rel === -1) return 'prev';
  if (rel === 0) return 'current';
  if (rel === 1) return 'next';
  return 'other';
}

function trayRoleLabel(role) {
  if (role === 'prev') return 'PREV';
  if (role === 'current') return 'CURRENT';
  if (role === 'next') return 'NEXT';
  return '';
}

function trayDropTarget(beltOffset) {
  const off = beltOffset ?? getFixedBeltOffset();
  return {
    x: trayXAtSlot(1, off) + 28,
    y: CONVEYOR.trayY + 72,
  };
}

function activeTrayDropTarget() {
  return trayDropTarget(getFixedBeltOffset());
}

function queueGridMetrics() {
  const { trackX, trackY, trackW, trackH, cols, rows, gap } = LOG_VIZ;
  const cellW = (trackW - (cols - 1) * gap) / cols;
  const cellH = (trackH - (rows - 1) * gap) / rows;
  return { trackX, trackY, cellW, cellH, cols, rows, gap };
}

function queueCellAt(index) {
  const g = queueGridMetrics();
  const row = Math.floor(index / g.cols);
  const col = index % g.cols;
  const x = g.trackX + col * (g.cellW + g.gap);
  const y = g.trackY + row * (g.cellH + g.gap);
  return {
    x, y,
    w: g.cellW,
    h: g.cellH,
    cx: x + g.cellW / 2,
    cy: y + g.cellH / 2,
  };
}


function formatTrayTime(ms) {
  return new Date(ms).toISOString().slice(11, 19);
}

/** Log time without trailing Z — e.g. 12:02:39.398 */
function formatLogTime(ms) {
  return new Date(ms).toISOString().slice(11, 23);
}

function renderTrayAt(id, windowStart, meta, now, balls, x, role, centerStart, extraClass = '') {
  if (!role || role === 'other') return '';

  const W = CONVEYOR.trayW;
  const H = CONVEYOR.trayH;
  const algo = state[id]?.algo;
  const isActive = role === 'current';
  const allowedCount = balls.filter((b) => b.allowed).length;
  const limit = meta.limit;

  let remaining = limit;
  const label = trayRoleLabel(role);

  if (id === 'fixed' && algo?.vizState) {
    if (isActive) {
      remaining = algo.vizState(now).remaining;
    } else if (role === 'prev') {
      remaining = Math.max(0, limit - allowedCount);
    } else if (role === 'next') {
      remaining = limit;
    }
  }

  const roleClass = role !== 'current' ? ` cv-tray-${role}` : '';
  const activeClass = isActive ? ' cv-tray-active' : '';
  const remClass = remaining === 0 ? ' cv-rem-empty' : '';
  const usedPct = limit > 0 ? allowedCount / limit : 0;
  const barW = Math.max(0, (W - 24) * usedPct);

  const winStart = formatTrayTime(windowStart);
  const winEnd = formatTrayTime(windowStart + WINDOW_MS);
  const phasePct = isActive && centerStart != null
    ? Math.min(1, Math.max(0, (now - centerStart) / WINDOW_MS))
    : 0;

  const headerFill = role === 'current' ? 'cv-tray-header-current'
    : role === 'next' ? 'cv-tray-header-next' : 'cv-tray-header-prev';

  let tokensHtml = '';
  const cols = 5;
  const tokenPadX = 18;
  const tokenStep = (W - tokenPadX * 2) / (cols - 1);
  for (let i = 0; i < limit; i += 1) {
    const col = i % cols;
    const row = Math.floor(i / cols);
    const bx = tokenPadX + col * tokenStep;
    const by = 66 + row * 18;
    const filled = i < allowedCount;
    if (filled) {
      const b = balls[i];
      const cls = b?.allowed !== false ? 'cv-token-filled' : 'cv-token-deny';
      tokensHtml += `<circle cx="${bx}" cy="${by}" r="5" class="${cls}"/>`;
    } else {
      tokensHtml += `<circle cx="${bx}" cy="${by}" r="5" class="cv-token-empty"/>`;
    }
  }

  const remLabel = role === 'next' ? 'fresh' : 'remaining';
  const remValue = role === 'next' ? limit : remaining;

  return `
    <g class="cv-tray${roleClass}${activeClass}${extraClass}" transform="translate(${x}, ${CONVEYOR.trayY})">
      <rect x="0" y="0" width="${W}" height="${H}" rx="10" class="cv-tray-body"/>
      ${isActive ? `<rect x="0" y="0" width="${W}" height="${H}" rx="10" class="cv-tray-active-ring"/>` : ''}

      <rect x="0" y="0" width="${W}" height="26" rx="10" class="cv-tray-header ${headerFill}"/>
      <rect x="0" y="16" width="${W}" height="10" class="cv-tray-header ${headerFill}"/>
      <text x="12" y="17" class="cv-tray-badge">${label}</text>
      <text x="${W - 12}" y="17" text-anchor="end" class="cv-tray-window-tag">60s window</text>

      <text x="12" y="40" class="cv-tray-time-range">${winStart} → ${winEnd}</text>

      <text x="${W - 12}" y="40" text-anchor="end" class="cv-tray-count">${allowedCount}<tspan class="cv-tray-count-sep"> / </tspan>${limit}</text>
      <text x="${W - 12}" y="52" text-anchor="end" class="cv-tray-count-label">used · limit</text>

      <line x1="12" y1="58" x2="${W - 12}" y2="58" class="cv-tray-divider"/>

      <rect x="12" y="${H - 34}" width="${W - 24}" height="5" rx="2.5" class="cv-tray-bar-bg"/>
      <rect x="12" y="${H - 34}" width="${barW}" height="5" rx="2.5" class="cv-tray-bar-fill${remClass ? ' cv-tray-bar-full' : ''}"/>
      <g class="cv-tray-tokens">${tokensHtml}</g>

      ${isActive ? `<rect x="0" y="0" width="${W * phasePct}" height="3" rx="1.5" class="cv-tray-progress"/>` : ''}

      <rect x="8" y="${H - 24}" width="${W - 16}" height="18" rx="6" class="cv-tray-rem-bg"/>
      <text x="16" y="${H - 11}" class="cv-tray-rem-label">${remLabel}</text>
      <text x="${W - 16}" y="${H - 10}" text-anchor="end" class="cv-tray-rem${remClass}">${remValue}</text>
    </g>`;
}

function renderTray(id, windowStart, meta, now, balls, centerStart, rollOffset) {
  const role = trayRoleForStart(windowStart, centerStart);
  const x = trayXAtSlot(traySlotIndex(role), rollOffset);
  return renderTrayAt(id, windowStart, meta, now, balls, x, role, centerStart);
}

function conveyorVizHtml(id) {
  if (id !== 'fixed') return '';
  return `
    <div class="conveyor-viz" id="viz-${id}">
      ${vizLegendHtml()}
      <p class="viz-steps">3 trays on belt · fixed refill pipe · trays move with lab clock · roll every 60s</p>
      <div class="viz-status viz-status-conveyor" id="fixed-status">checking rem…</div>
      <p class="fixed-roll-hint" id="fixed-roll-hint"></p>
      <svg class="conveyor-svg" viewBox="0 0 ${CONVEYOR.viewW} ${CONVEYOR.viewH}" width="${CONVEYOR.viewW}" height="${CONVEYOR.viewH}">
        <defs>
          <pattern id="belt-stripes-${id}" width="16" height="8" patternUnits="userSpaceOnUse">
            <rect width="16" height="8" fill="#78716c"/>
            <rect width="8" height="8" fill="#57534e"/>
          </pattern>
          <clipPath id="fixed-belt-clip">
            <rect x="${CONVEYOR.beltOriginX - 4}" y="${CONVEYOR.trayY - 8}" width="${CONVEYOR.trayW * 3 + 8}" height="${CONVEYOR.trayH + CONVEYOR.beltY - CONVEYOR.trayY + 20}"/>
          </clipPath>
        </defs>
        <g id="fixed-pipe" transform="translate(${currentPipeX()}, 0)">
          ${refillPipeMarkup(CONVEYOR.pipeTop)}
        </g>
        <rect x="${CONVEYOR.beltOriginX - 2}" y="${CONVEYOR.trayY - 6}" width="${CONVEYOR.trayW * 3 + 4}" height="${CONVEYOR.beltY - CONVEYOR.trayY + 18}" rx="12" class="cv-belt-frame"/>
        <rect x="${CONVEYOR.beltOriginX}" y="${CONVEYOR.beltY}" width="${CONVEYOR.trayW * 3}" height="12" rx="3" class="cv-belt"/>
        <rect x="${CONVEYOR.beltOriginX}" y="${CONVEYOR.beltY}" width="${CONVEYOR.trayW * 3}" height="12" rx="3" fill="url(#belt-stripes-${id})" class="cv-belt-texture" id="${id}-belt-texture"/>
        <g clip-path="url(#fixed-belt-clip)">
          <g id="${id}-trays"></g>
        </g>
        <g id="${id}-drops"></g>
      </svg>
    </div>`;
}

function queueVizHtml() {
  const g = queueGridMetrics();
  return `
    <div class="queue-viz" id="viz-log">
      ${vizLegendHtml()}
      <p class="viz-steps">Large sliding window · FIFO queue · each cell shows insert timestamp</p>
      <div class="viz-status" id="log-status">poll HEAD → size=0 → rem=15</div>
      <svg class="queue-svg" viewBox="0 0 ${LOG_VIZ.viewW} ${LOG_VIZ.viewH}" width="${LOG_VIZ.viewW}" height="${LOG_VIZ.viewH}">
        <defs>
          <linearGradient id="queue-window-fill" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="#2563eb" stop-opacity="0.05"/>
            <stop offset="100%" stop-color="#2563eb" stop-opacity="0.12"/>
          </linearGradient>
        </defs>
        ${slimPipeMarkup(LOG_VIZ.pipeX, 8)}
        <text x="${LOG_VIZ.pipeX}" y="6" text-anchor="middle" class="cv-label">req</text>
        <line x1="${LOG_VIZ.pipeX}" y1="28" x2="${LOG_VIZ.pipeX}" y2="${LOG_VIZ.trackY - 6}" class="slim-pipe-guide"/>

        <rect x="24" y="36" width="${LOG_VIZ.trackW + 24}" height="${LOG_VIZ.trackH + 28}" rx="14" class="queue-frame"/>
        <rect x="${LOG_VIZ.trackX}" y="${LOG_VIZ.trackY}" width="${LOG_VIZ.trackW}" height="${LOG_VIZ.trackH}" rx="10" fill="url(#queue-window-fill)" class="queue-window-band"/>
        <text x="${LOG_VIZ.pipeX}" y="50" text-anchor="middle" class="queue-window-label" id="log-window-label">sliding window 60s · limit 15</text>

        <text x="${LOG_VIZ.trackX}" y="${LOG_VIZ.trackY - 6}" class="queue-head-label">◀ HEAD poll()</text>
        <text x="${LOG_VIZ.trackX + LOG_VIZ.trackW}" y="${LOG_VIZ.trackY - 6}" text-anchor="end" class="queue-tail-label">TAIL offer() ▶</text>

        <g id="log-grid-lines">
          ${Array.from({ length: g.rows }, (_, r) => Array.from({ length: g.cols }, (_, c) => {
            const cell = queueCellAt(r * g.cols + c);
            return `<rect x="${cell.x}" y="${cell.y}" width="${cell.w}" height="${cell.h}" rx="8" class="queue-cell-empty"/>`;
          }).join('')).join('')}
        </g>
        <g id="log-queue-items"></g>
        <g id="log-drops"></g>
        <text id="log-queue-size" x="${LOG_VIZ.trackX}" y="${LOG_VIZ.trackY + LOG_VIZ.trackH + 22}" class="queue-meta">size: 0</text>
        <text id="log-rem" x="${LOG_VIZ.trackX + LOG_VIZ.trackW}" y="${LOG_VIZ.trackY + LOG_VIZ.trackH + 22}" text-anchor="end" class="queue-rem">rem 15</text>
        <text id="log-prune-hint" x="${LOG_VIZ.pipeX}" y="${LOG_VIZ.trackY + LOG_VIZ.trackH + 22}" text-anchor="middle" class="queue-prune-hint"></text>
      </svg>
    </div>`;
}

function counterVizHtml() {
  return `
    <div class="counter-viz" id="viz-counter">
      ${vizLegendHtml()}
      <p class="viz-steps">① Weight prev window &nbsp;→&nbsp; ② Add currentCount &nbsp;→&nbsp; ③ On roll: current → prev</p>
      <div class="viz-status" id="counter-status">estimated = weight + current</div>
      <svg class="counter-svg" viewBox="0 0 520 280" width="520" height="280">
        <defs>
          <linearGradient id="prev-bar-grad" x1="0" y1="0" x2="1" y2="0">
            <stop offset="0%" stop-color="#8b5cf6"/>
            <stop offset="100%" stop-color="#a78bfa"/>
          </linearGradient>
          <linearGradient id="curr-bar-grad" x1="0" y1="0" x2="1" y2="0">
            <stop offset="0%" stop-color="#0284c7"/>
            <stop offset="100%" stop-color="#38bdf8"/>
          </linearGradient>
        </defs>
        <text x="260" y="14" text-anchor="middle" class="cv-label">incoming request</text>
        <rect x="246" y="18" width="28" height="26" rx="4" class="cv-pipe"/>
        <line x1="260" y1="44" x2="260" y2="58" class="cv-pipe-line"/>
        <line x1="440" y1="68" x2="440" y2="210" class="counter-limit-line"/>
        <text x="444" y="76" class="counter-limit-label">limit</text>

        <text x="72" y="78" class="counter-bar-label">prevCount</text>
        <text x="72" y="92" class="counter-bar-sub">previous fixed window</text>
        <rect x="72" y="98" width="360" height="28" rx="6" class="counter-bar-bg"/>
        <rect x="72" y="98" width="0" height="28" rx="6" class="counter-bar-weighted" id="counter-weighted-fill"/>
        <rect x="72" y="98" width="0" height="28" rx="6" class="counter-bar-prev" id="counter-prev-fill"/>
        <text x="76" y="116" class="counter-bar-inner" id="counter-prev-inner"></text>
        <text x="436" y="116" text-anchor="end" class="counter-bar-val" id="counter-prev-label">0</text>

        <g id="counter-roll-group">
          <path d="M 256 132 L 256 148" class="counter-roll-path"/>
          <polygon points="250,148 256,158 262,148" class="counter-roll-path"/>
          <text x="256" y="128" text-anchor="middle" class="counter-roll-arrow" id="counter-roll-arrow">window roll</text>
        </g>

        <text x="72" y="178" class="counter-bar-label">currentCount</text>
        <text x="72" y="192" class="counter-bar-sub">current fixed window</text>
        <rect x="72" y="198" width="360" height="28" rx="6" class="counter-bar-bg"/>
        <rect x="72" y="198" width="0" height="28" rx="6" class="counter-bar-curr" id="counter-curr-fill"/>
        <text x="76" y="216" class="counter-bar-inner" id="counter-curr-inner"></text>
        <text x="436" y="216" text-anchor="end" class="counter-bar-val" id="counter-curr-label">0</text>

        <rect x="72" y="238" width="360" height="10" rx="5" class="counter-progress-bg"/>
        <rect x="72" y="238" width="0" height="10" rx="5" class="counter-progress-fill" id="counter-window-progress"/>
        <text x="72" y="234" class="counter-bar-sub" id="counter-progress-label">window progress</text>

        <text x="260" y="262" text-anchor="middle" class="counter-formula" id="counter-formula"></text>
        <text x="260" y="276" text-anchor="middle" class="counter-rollover-msg" id="counter-rollover-msg"></text>
        <rect id="counter-roll-ghost" x="72" y="198" width="0" height="28" rx="6" class="counter-roll-ghost" opacity="0"/>
      </svg>
    </div>`;
}

function ballsForTray(id, windowStart) {
  const s = state[id];
  if (!s?.vizBalls) return [];
  return s.vizBalls.filter((b) => b.trayStart === windowStart);
}

function trayStartForRequest(id, reqMs) {
  const algo = state[id]?.algo;
  if (!algo?.vizState) {
    return Math.floor((reqMs - labStartMs) / WINDOW_MS) * WINDOW_MS + labStartMs;
  }
  return algo.vizState(reqMs).windowStart;
}

function updateConveyorVisuals() {
  const now = nowMs();
  const id = 'fixed';
  const meta = ALGOS.find((a) => a.id === id);
  const traysEl = document.getElementById(`${id}-trays`);
  if (!traysEl || !meta) return;

  const s = state[id];
  const algo = s?.algo;
  let centerStart;
  if (algo?.vizState) {
    centerStart = algo.vizState(now).windowStart;
  } else {
    centerStart = Math.floor((now - labStartMs) / WINDOW_MS) * WINDOW_MS + labStartMs;
  }

  if (s.lastWindowStart != null && centerStart !== s.lastWindowStart && !s.beltRoll?.active) {
    triggerFixedBeltRoll(s.lastWindowStart);
  }
  if (s.lastWindowStart !== centerStart) {
    s.lastWindowStart = centerStart;
  }

  const { offset: rollOffset, rolling } = getBeltRollOffset();
  const drift = rolling ? 0 : windowDriftOffset(now, centerStart);
  const beltOffset = rolling ? rollOffset : drift;
  const trays = [];

  if (rolling && s.beltRoll?.oldCenterStart != null) {
    const os = s.beltRoll.oldCenterStart;
    const rollWindows = [
      { ws: os - WINDOW_MS, role: 'prev' },
      { ws: os, role: 'current' },
      { ws: os + WINDOW_MS, role: 'next' },
      { ws: os + 2 * WINDOW_MS, role: 'next' },
    ];
    rollWindows.forEach((entry, i) => {
      const balls = ballsForTray(id, entry.ws);
      const x = trayXAtSlot(i, beltOffset);
      const displayRole = i === 3 ? 'next' : entry.role;
      const extra = i === 0 ? ' cv-tray-exiting' : i === 3 ? ' cv-tray-entering' : '';
      trays.push(renderTrayAt(id, entry.ws, meta, now, balls, x, displayRole, os + WINDOW_MS, extra));
    });
  } else {
    CONVEYOR.traySlots.forEach((slot) => {
      const ws = centerStart + slot * WINDOW_MS;
      const balls = ballsForTray(id, ws);
      trays.push(renderTray(id, ws, meta, now, balls, centerStart, beltOffset));
    });
  }

  traysEl.innerHTML = trays.join('');

  const beltEl = document.getElementById(`${id}-belt-texture`);
  if (beltEl) {
    const stripeShift = rolling
      ? Math.abs(beltOffset) % 16
      : (Math.max(0, now - centerStart) / WINDOW_MS) * 16 % 16;
    beltEl.setAttribute('transform', `translate(${-stripeShift}, 0)`);
  }

  const computeEl = document.getElementById('fixed-status');
  if (computeEl && !computeEl.classList.contains('viz-status-flash') && algo?.vizState) {
    const vs = algo.vizState(now);
    const secLeft = Math.max(0, Math.ceil((centerStart + WINDOW_MS - now) / 1000));
    const speedTag = labSpeed > 1 ? ` · ${labSpeed}×` : '';
    computeEl.textContent = rolling
      ? `↻ belt rolling…${speedTag}`
      : `rem=${vs.remaining} / ${vs.limit} · roll in ${secLeft}s${speedTag}`;
  }

  processDropQueues('fixed');
}

function updateQueueVisuals() {
  const algo = state.log?.algo;
  const meta = ALGOS.find((a) => a.id === QUEUE_ID);
  const itemsEl = document.getElementById('log-queue-items');
  const remEl = document.getElementById('log-rem');
  const sizeEl = document.getElementById('log-queue-size');
  const pruneHint = document.getElementById('log-prune-hint');
  const windowLabel = document.getElementById('log-window-label');
  if (!algo?.vizState || !itemsEl || !meta) return;

  const now = nowMs();
  const vs = algo.vizState(now);
  const s = state.log;
  const prevTs = s.lastTimestamps || [];
  const currTs = vs.timestamps;
  const pruned = prevTs.filter((t) => !currTs.includes(t));

  if (windowLabel) {
    windowLabel.textContent =
      `window (${formatTrayTime(vs.windowStart).slice(11)} → ${formatTrayTime(now).slice(11)}]`;
  }

  let html = '';
  currTs.forEach((t, i) => {
    const cell = queueCellAt(i);
    const tsFull = formatLogTime(t);
    const isHead = i === 0;
    const isTail = i === currTs.length - 1;
    const pruneClass = isHead && pruned.length ? ' queue-item-prune' : '';
    const headClass = isHead ? ' queue-item-head' : '';
    const tailClass = isTail ? ' queue-item-tail' : '';
    const role = isHead ? 'HEAD' : isTail ? 'TAIL' : `#${i + 1}`;
    html += `
      <g class="queue-item${pruneClass}${headClass}${tailClass}" transform="translate(${cell.x}, ${cell.y})">
        <rect width="${cell.w}" height="${cell.h}" rx="8" class="queue-cell"/>
        <text x="10" y="16" class="queue-role">${role}</text>
        <circle cx="${cell.w / 2}" cy="${cell.h / 2 - 6}" r="9" class="cv-ball-ok"/>
        <text x="${cell.w / 2}" y="${cell.h - 28}" text-anchor="middle" class="queue-ts-label">inserted at</text>
        <text x="${cell.w / 2}" y="${cell.h - 14}" text-anchor="middle" class="queue-ts-full">${tsFull}</text>
      </g>`;
  });

  if (!currTs.length) {
    html += `<text x="${LOG_VIZ.pipeX}" y="${LOG_VIZ.trackY + LOG_VIZ.trackH / 2}" text-anchor="middle" class="queue-empty">queue empty — send a request</text>`;
  }

  itemsEl.innerHTML = html;
  if (remEl) remEl.textContent = `rem ${vs.remaining}`;
  if (sizeEl) sizeEl.textContent = `size: ${vs.used} / ${vs.limit}`;
  if (pruneHint) {
    pruneHint.textContent = pruned.length
      ? `↑ polled ${pruned.length} expired stamp(s) from HEAD`
      : '';
  }

  const computeEl = document.getElementById('log-status');
  if (computeEl && !computeEl.classList.contains('viz-status-flash')) {
    computeEl.textContent = `poll HEAD → size=${vs.used} → rem=${vs.remaining}`;
  }

  s.lastTimestamps = [...currTs];
  processDropQueues('log');
}

function updateCounterVisuals() {
  const algo = state.counter?.algo;
  const meta = ALGOS.find((a) => a.id === COUNTER_ID);
  if (!algo?.vizState || !meta) return;

  const now = nowMs();
  const vs = algo.vizState(now);
  const s = state.counter;
  const barMaxW = 360;
  const barX = 72;
  const limit = meta.limit;

  const prevFill = document.getElementById('counter-prev-fill');
  const currFill = document.getElementById('counter-curr-fill');
  const weightedFill = document.getElementById('counter-weighted-fill');
  const progressFill = document.getElementById('counter-window-progress');
  const prevLabel = document.getElementById('counter-prev-label');
  const currLabel = document.getElementById('counter-curr-label');
  const prevInner = document.getElementById('counter-prev-inner');
  const currInner = document.getElementById('counter-curr-inner');
  const formulaEl = document.getElementById('counter-formula');
  const rollArrow = document.getElementById('counter-roll-arrow');
  const progressLabel = document.getElementById('counter-progress-label');

  const prevW = (vs.prevCount / limit) * barMaxW;
  const currW = (vs.currentCount / limit) * barMaxW;
  const weightedW = (vs.weightedPrev / limit) * barMaxW;

  if (prevFill) prevFill.setAttribute('width', String(prevW));
  if (currFill) currFill.setAttribute('width', String(currW));
  if (weightedFill) weightedFill.setAttribute('width', String(weightedW));
  if (prevLabel) prevLabel.textContent = `${vs.prevCount}`;
  if (currLabel) currLabel.textContent = `${vs.currentCount}`;
  if (prevInner) prevInner.textContent = vs.prevCount > 0 ? `${vs.prevCount}` : '';
  if (currInner) currInner.textContent = vs.currentCount > 0 ? `${vs.currentCount}` : '';

  const elapsed = Math.max(0, now - vs.currentStart);
  const realProgress = Math.min(1, elapsed / WINDOW_MS);
  if (progressFill) progressFill.setAttribute('width', String(realProgress * barMaxW));
  if (progressLabel) {
    const secLeft = Math.max(0, Math.ceil((WINDOW_MS - elapsed) / 1000));
    progressLabel.textContent = `window progress · ${secLeft}s until roll`;
  }

  if (formulaEl) {
    formulaEl.textContent = `limit ${limit} · rem ${vs.remaining}`;
  }

  if (s.lastCurrentStart != null && s.lastCurrentStart !== vs.currentStart) {
    triggerCounterRollover(s.lastCurrentCount, vs.prevCount, barX, barMaxW, limit);
  }

  if (rollArrow && !document.getElementById('viz-counter')?.classList.contains('counter-rollover-flash')) {
    rollArrow.textContent =
      s.lastCurrentCount != null && s.lastCurrentCount > 0
        ? `on roll: ${s.lastCurrentCount} → prevCount`
        : 'window roll';
  }

  s.lastCurrentStart = vs.currentStart;
  s.lastCurrentCount = vs.currentCount;
  s.lastPrevCount = vs.prevCount;

  const computeEl = document.getElementById('counter-status');
  if (computeEl && !computeEl.classList.contains('viz-status-flash')) {
    computeEl.textContent =
      `estimated = (${vs.prevCount} × ${vs.overlapPct}%) + ${vs.currentCount} = ${vs.estimated}`;
  }

  processDropQueues('counter');
}

function triggerCounterRollover(oldCurrent, newPrev, barX, barMaxW, limit) {
  const msg = document.getElementById('counter-rollover-msg');
  const viz = document.getElementById('viz-counter');
  const rollArrow = document.getElementById('counter-roll-arrow');
  const ghost = document.getElementById('counter-roll-ghost');

  if (msg) {
    msg.textContent =
      `Window rolled! currentCount (${oldCurrent}) → prevCount · currentCount reset to 0`;
    msg.classList.add('counter-rollover-show');
  }
  if (rollArrow) {
    rollArrow.textContent = `↻ prevCount ← ${newPrev}`;
    rollArrow.classList.add('counter-roll-arrow-active');
  }
  if (ghost && oldCurrent > 0) {
    const ghostW = (oldCurrent / limit) * barMaxW;
    ghost.setAttribute('x', String(barX));
    ghost.setAttribute('y', '198');
    ghost.setAttribute('width', String(ghostW));
    ghost.setAttribute('opacity', '0.85');
    requestAnimationFrame(() => {
      ghost.setAttribute('y', '98');
      ghost.setAttribute('opacity', '0');
    });
  }
  viz?.classList.add('counter-rollover-flash');
  setTimeout(() => {
    msg?.classList.remove('counter-rollover-show');
    viz?.classList.remove('counter-rollover-flash');
    rollArrow?.classList.remove('counter-roll-arrow-active');
    ghost?.setAttribute('opacity', '0');
  }, 2800);
}

function updateSpecialVisuals() {
  updateQueueVisuals();
  updateCounterVisuals();
}

function processDropQueues(id) {
  const queue = dropQueues[id];
  if (!queue?.length) return;

  const t = Date.now();
  while (queue.length && queue[0].startAt <= t) {
    const item = queue.shift();
    if (id === 'fixed') spawnConveyorDrop(item);
    else if (id === 'log') spawnQueueDrop(item);
    else if (id === 'counter') spawnCounterDrop(item);
  }
}

function showComputeFlash(el, item) {
  if (!el) return;
  el.textContent = item.computeText;
  el.classList.remove('viz-status-ok', 'viz-status-deny');
  el.classList.add('viz-status-flash', item.allowed ? 'viz-status-ok' : 'viz-status-deny');
  setTimeout(() => {
    el.classList.remove('viz-status-flash', 'viz-status-ok', 'viz-status-deny');
  }, 900);
}

function spawnConveyorDrop(item) {
  const dropsEl = document.getElementById('fixed-drops');
  const statusEl = document.getElementById('fixed-status');
  if (!dropsEl) return;

  showComputeFlash(statusEl, item);

  const beltOffset = getFixedBeltOffset();
  const pipeX = currentPipeX();
  const target = trayDropTarget(beltOffset);
  const landX = item.allowed ? target.x : pipeX;
  const landY = item.allowed ? target.y : CONVEYOR.pipeTop + 36;

  const ballId = `drop-fixed-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`;
  const cls = item.allowed ? 'cv-ball-ok' : 'cv-ball-deny';
  const g = document.createElementNS('http://www.w3.org/2000/svg', 'g');
  g.setAttribute('class', 'cv-falling-ball');
  g.innerHTML = `
    <circle id="${ballId}" cx="${pipeX}" cy="${CONVEYOR.pipeTop + 18}" r="7" class="cv-ball cv-ball-pending"/>
    <text x="${pipeX}" y="${CONVEYOR.pipeTop + 14}" text-anchor="middle" class="cv-drop-tag">${item.allowed ? 'ALLOW' : 'DENY'}</text>
  `;
  dropsEl.appendChild(g);

  updateConveyorVisuals();

  setTimeout(() => {
    const ball = document.getElementById(ballId);
    if (!ball) return;
    ball.classList.remove('cv-ball-pending');
    ball.classList.add('cv-ball', cls);
    ball.setAttribute('cx', String(landX));
    ball.setAttribute('cy', String(landY));
  }, 200);

  setTimeout(() => {
    g.remove();
    updateConveyorVisuals();
  }, 700);
}

function spawnQueueDrop(item) {
  const dropsEl = document.getElementById('log-drops');
  const statusEl = document.getElementById('log-status');
  if (!dropsEl) return;

  showComputeFlash(statusEl, item);

  const ballId = `drop-log-${Date.now()}-${Math.random().toString(36).slice(2, 5)}`;
  const cls = item.allowed ? 'cv-ball-ok' : 'cv-ball-deny';
  const cell = item.queueIndex >= 0 ? queueCellAt(item.queueIndex) : null;
  const tailX = cell ? cell.cx : LOG_VIZ.pipeX;
  const landY = item.allowed && cell ? cell.cy : 36;

  const g = document.createElementNS('http://www.w3.org/2000/svg', 'g');
  g.setAttribute('class', 'cv-falling-ball');
  g.innerHTML = `
    <circle id="${ballId}" cx="${LOG_VIZ.pipeX}" cy="28" r="7" class="cv-ball cv-ball-pending"/>
    <text x="${LOG_VIZ.pipeX}" y="24" text-anchor="middle" class="cv-drop-tag">${item.allowed ? 'ALLOW' : 'DENY'}</text>
  `;
  dropsEl.appendChild(g);

  updateQueueVisuals();

  setTimeout(() => {
    const ball = document.getElementById(ballId);
    if (!ball) return;
    ball.classList.remove('cv-ball-pending');
    ball.classList.add('cv-ball', cls);
    ball.setAttribute('cx', String(item.allowed ? tailX : LOG_VIZ.pipeX));
    ball.setAttribute('cy', String(landY));
  }, 200);

  setTimeout(() => {
    g.remove();
    updateQueueVisuals();
  }, 700);
}

function spawnCounterDrop(item) {
  const statusEl = document.getElementById('counter-status');
  showComputeFlash(statusEl, item);

  const viz = document.getElementById('viz-counter');
  viz?.classList.remove('counter-pulse');
  void viz?.offsetWidth;
  viz?.classList.add('counter-pulse');

  setTimeout(() => viz?.classList.remove('counter-pulse'), 500);
  updateCounterVisuals();
}

function queueVizDrops(id, dropMeta) {
  if (!dropQueues[id]) return;

  const base = Date.now();
  dropMeta.forEach((entry, i) => {
    const d = entry.decision;
    let computeText;
    if (id === 'log') {
      computeText = `size < limit? → ${d.allowed ? 'ALLOW offer()' : 'DENY'} rem=${d.remainingTokens}`;
    } else if (id === 'counter') {
      computeText = `estimated < limit? → ${d.allowed ? 'ALLOW' : 'DENY'} rem=${d.remainingTokens}`;
    } else {
      const beforeRem = d.allowed ? d.remainingTokens + 1 : d.remainingTokens;
      computeText = `rem ${beforeRem}? → ${d.allowed ? 'ALLOW' : 'DENY'} rem=${d.remainingTokens}`;
    }

    dropQueues[id].push({
      allowed: d.allowed,
      computeText,
      queueIndex: entry.queueIndex ?? -1,
      trayStart: entry.trayStart ?? null,
      startAt: base + i * 180,
    });
  });
}

function burstSize(meta) {
  return meta.limit + 5;
}

async function loadLldSection() {
  const el = document.getElementById('lld-content');
  if (!el) return;

  el.className = 'lld-doc lld-doc-loading';
  el.textContent = 'Loading LLD…';

  try {
    const res = await fetch('./rate-limiter-lld-embed.html');
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    el.className = 'lld-doc';
    el.innerHTML = await res.text();
  } catch (err) {
    el.className = 'lld-doc lld-doc-error';
    el.innerHTML = 'Could not load LLD section. Serve via HTTP (see README GitHub Pages steps).';
    console.warn('LLD embed load failed:', err);
  }
}

function init() {
  labStartMs = Date.parse(LAB_START_ISO);
  pageOpenMs = Date.now();
  const start = nowMs();

  ALGOS.forEach((algo) => {
    state[algo.id] = {
      algo: algo.create(start),
      reqCount: 0,
      allowCount: 0,
      denyCount: 0,
      log: [],
      vizBalls: [],
    };
    if (CONVEYOR_IDS.includes(algo.id)) {
      dropQueues[algo.id] = [];
      const inst = state[algo.id].algo;
      state[algo.id].lastWindowStart = inst.vizState?.(start).windowStart ?? start;
      state[algo.id].beltRoll = null;
    }
    if (algo.id === QUEUE_ID) {
      dropQueues.log = [];
      state[algo.id].lastTimestamps = [];
    }
    if (algo.id === COUNTER_ID) {
      dropQueues.counter = [];
      const st = state[algo.id];
      st.lastCurrentStart = null;
      st.lastCurrentCount = 0;
      st.lastPrevCount = 0;
    }
  });

  renderSections();
  updateSpeedButtons();
  loadLldSection();
  tickClock();
}

function tickClock() {
  const el = document.getElementById('lab-clock');
  if (el) el.textContent = formatTime(nowMs());
  updateBucketVisuals();
  updateConveyorVisuals();
  updateSpecialVisuals();
}

function algoVizHtml(id) {
  if (id === 'fixed') return conveyorVizHtml(id);
  if (id === QUEUE_ID) return queueVizHtml();
  if (id === COUNTER_ID) return counterVizHtml();
  return bucketVizHtml(id);
}

function bucketVizHtml(id) {
  if (id === 'token') {
    return `
      <div class="bucket-viz" id="viz-token">
        ${vizLegendHtml()}
        <p class="viz-steps">Refill pipe adds tokens · each request spends one token</p>
        <svg class="bucket-svg" viewBox="0 0 200 170" width="200" height="170">
          <text x="100" y="12" text-anchor="middle" class="viz-label">refill pipe</text>
          <rect x="92" y="16" width="16" height="28" rx="2" class="pipe"/>
          <circle class="token-drop token-drop-a" cx="100" cy="22" r="5"/>
          <circle class="token-drop token-drop-b" cx="100" cy="22" r="5"/>
          <path d="M 55 52 L 55 130 Q 55 148 100 148 Q 145 148 145 130 L 145 52 Z" class="bucket-shell"/>
          <clipPath id="token-fill-clip"><path d="M 59 56 L 59 126 Q 59 142 100 142 Q 141 142 141 126 L 141 56 Z"/></clipPath>
          <g clip-path="url(#token-fill-clip)">
            <rect id="token-fill" x="59" y="142" width="82" height="0" class="token-fill"/>
          </g>
          <text x="100" y="164" text-anchor="middle" class="viz-count" id="token-count">10 / 10</text>
        </svg>
      </div>`;
  }
  if (id === 'leaky') {
    return `
      <div class="bucket-viz" id="viz-leaky">
        ${vizLegendHtml()}
        <p class="viz-steps">Requests pour in · leak pipe drips at steady rate</p>
        <svg class="bucket-svg" viewBox="0 0 220 170" width="220" height="170">
          <text x="28" y="78" class="viz-label">req</text>
          <rect x="4" y="82" width="36" height="12" rx="2" class="pipe req-pipe"/>
          <circle class="req-drop" id="leaky-req-drop" cx="4" cy="88" r="4"/>
          <path d="M 60 52 L 60 118 Q 60 136 110 136 Q 160 136 160 118 L 160 52 Z" class="bucket-shell"/>
          <clipPath id="leaky-fill-clip"><path d="M 64 56 L 64 114 Q 64 130 110 130 Q 156 130 156 114 L 156 56 Z"/></clipPath>
          <g clip-path="url(#leaky-fill-clip)">
            <rect id="leaky-fill" x="64" y="130" width="92" height="0" class="leak-fill"/>
          </g>
          <rect x="102" y="136" width="16" height="22" rx="2" class="pipe leak-pipe"/>
          <circle class="leak-drip leak-drip-a" cx="110" cy="158" r="3"/>
          <circle class="leak-drip leak-drip-b" cx="110" cy="158" r="3"/>
          <text x="110" y="12" text-anchor="middle" class="viz-label">leak pipe</text>
          <text x="110" y="164" text-anchor="middle" class="viz-count" id="leaky-count">0 / 10</text>
        </svg>
      </div>`;
  }
  return '';
}

function updateBucketVisuals(triggerReqAnim) {
  const reqMs = nowMs();

  const tokenAlgo = state.token?.algo;
  if (tokenAlgo?.snapshot) {
    const snap = tokenAlgo.snapshot(reqMs);
    const fill = document.getElementById('token-fill');
    const label = document.getElementById('token-count');
    if (fill && label) {
      const maxH = 86;
      const h = Math.max(0, snap.level * maxH);
      fill.setAttribute('y', String(142 - h));
      fill.setAttribute('height', String(h));
      label.textContent = `${snap.count} / ${snap.capacity} tokens`;
      const viz = document.getElementById('viz-token');
      viz?.querySelectorAll('.token-drop').forEach((el) => {
        el.style.opacity = snap.level >= 0.99 ? '0.25' : '1';
      });
    }
  }

  const leakyAlgo = state.leaky?.algo;
  if (leakyAlgo?.snapshot) {
    const snap = leakyAlgo.snapshot(reqMs);
    const fill = document.getElementById('leaky-fill');
    const label = document.getElementById('leaky-count');
    if (fill && label) {
      const maxH = 74;
      const h = Math.max(0, snap.level * maxH);
      fill.setAttribute('y', String(130 - h));
      fill.setAttribute('height', String(h));
      label.textContent = `${snap.count} / ${snap.capacity} water`;
    }
    if (triggerReqAnim) {
      const viz = document.getElementById('viz-leaky');
      const drop = document.getElementById('leaky-req-drop');
      viz?.classList.remove('req-splash');
      drop?.classList.remove('req-fly');
      void viz?.offsetWidth;
      viz?.classList.add('req-splash');
      drop?.classList.add('req-fly');
    }
  }
}

function prosConsHtml(meta) {
  const list = (items) => items.map((item) => `<li>${item}</li>`).join('');
  return `
    <div class="pros-cons">
      <div>
        <h3>Pros</h3>
        <ul>${list(meta.pros)}</ul>
      </div>
      <div>
        <h3>Cons</h3>
        <ul>${list(meta.cons)}</ul>
      </div>
    </div>`;
}

function renderSections() {
  const root = document.getElementById('playground-sections');
  root.innerHTML = ALGOS.map((meta) => {
    const burst = burstSize(meta);
    const viz = algoVizHtml(meta.id);
    const hasVizPanel = VIZ_PANEL_IDS.includes(meta.id);
    const vizBlock = viz
      ? `<div class="demo-viz">${viz}</div>`
      : '';
    return `
    <section class="algo-section" id="${meta.id}" data-id="${meta.id}">
      <h2>${meta.name}</h2>
      <p class="algo-lede">${meta.lede}</p>
      <div class="config-badge">${meta.config} &middot; tier: ${meta.tier}</div>
      ${prosConsHtml(meta)}
      <div class="demo-panel${hasVizPanel ? ' has-conveyor' : ''}">
        ${vizBlock}
        <div class="demo-body">
          <div class="burst-hint">${meta.burstHint}</div>
          <div class="btn-row">
            <button type="button" class="btn" data-send="${meta.id}">Send request</button>
            <button type="button" class="btn btn-secondary" data-burst="${meta.id}">Burst &times;${burst}</button>
          </div>
          <div class="result idle" id="result-${meta.id}">
            <div class="status">Ready</div>
            Reset, then Burst &mdash; expect up to ${meta.limit} allows, then DENY
          </div>
          <div class="stats" id="stats-${meta.id}">requests: 0 | allowed: 0 | denied: 0</div>
          <ul class="log" id="log-${meta.id}"></ul>
        </div>
      </div>
    </section>
  `;
  }).join('');

  root.querySelectorAll('[data-send]').forEach((btn) => {
    btn.addEventListener('click', () => sendRequests(btn.dataset.send, 1));
  });
  root.querySelectorAll('[data-burst]').forEach((btn) => {
    btn.addEventListener('click', () => {
      const meta = ALGOS.find((a) => a.id === btn.dataset.burst);
      sendRequests(btn.dataset.burst, burstSize(meta));
    });
  });

  updateBucketVisuals();
  updateConveyorVisuals();
  updateSpecialVisuals();
}

function logEntryHtml(reqMs, d, tag) {
  const cls = d.allowed ? 'ok' : 'no';
  const tagHtml = tag
    ? `<span class="log-tag">${tag.trim()}</span>`
    : '';
  return `<li class="log-entry ${cls}">
    ${tagHtml}
    <span class="log-time">${formatLogTime(reqMs)}</span>
    <span class="log-verdict ${cls}">${d.allowed ? 'ALLOW' : 'DENY'}</span>
    <span class="log-rem">rem=${d.remainingTokens}</span>
  </li>`;
}

function pushLog(s, html) {
  s.log.unshift(html);
  while (s.log.length > LOG_MAX) s.log.pop();
}

function renderState(id, lastDecision, reqMs, burstSummary) {
  const s = state[id];
  const meta = ALGOS.find((a) => a.id === id);
  const d = lastDecision;

  const resultEl = document.getElementById(`result-${id}`);
  resultEl.className = `result ${d.allowed ? 'allow' : 'deny'}`;

  if (burstSummary) {
    const capHit = burstSummary.allowed === meta.limit && burstSummary.denied > 0;
    const capLine = capHit
      ? `<br>cap hit: req #${meta.limit + 1}+ DENY (max burst = ${meta.limit})`
      : '';
    resultEl.innerHTML = `
      <div class="status">Burst &times;${burstSummary.total} @ ${formatTime(reqMs).slice(11)}</div>
      allowed: ${burstSummary.allowed} | denied: ${burstSummary.denied}${capLine}<br>
      last: ${d.allowed ? '200 ALLOW' : '429 DENY'} | remaining: ${d.remainingTokens}<br>
      retryAt: ${formatRetry(d.retryAt)}
    `;
  } else {
    resultEl.innerHTML = `
      <div class="status">${d.allowed ? '200 ALLOW' : '429 DENY'}</div>
      remaining: ${d.remainingTokens}<br>
      retryAt: ${formatRetry(d.retryAt)}
    `;
  }

  document.getElementById(`stats-${id}`).textContent =
    `requests: ${s.reqCount} | allowed: ${s.allowCount} | denied: ${s.denyCount}`;

  document.getElementById(`log-${id}`).innerHTML = s.log.join('');
}

function sendRequests(id, count) {
  const s = state[id];
  const reqMs = nowMs();
  let lastDecision = null;
  let burstAllowed = 0;
  let burstDenied = 0;
  const decisions = [];
  const dropMeta = [];
  let batchTrayStart = null;

  for (let i = 0; i < count; i += 1) {
    if (CONVEYOR_IDS.includes(id) && batchTrayStart == null) {
      batchTrayStart = trayStartForRequest(id, reqMs);
    }
    const queueIndex = id === QUEUE_ID ? s.algo.requestLog.length : -1;
    const d = s.algo.tryAcquire(reqMs);
    lastDecision = d;
    decisions.push(d);
    dropMeta.push({
      decision: d,
      queueIndex: d.allowed ? queueIndex : -1,
      trayStart: batchTrayStart,
    });
    if (batchTrayStart != null) {
      s.vizBalls.push({ reqMs, allowed: d.allowed, trayStart: batchTrayStart });
    }
    s.reqCount += 1;
    if (d.allowed) {
      s.allowCount += 1;
      burstAllowed += 1;
    } else {
      s.denyCount += 1;
      burstDenied += 1;
    }
    const tag = count > 1 ? `#${i + 1}` : '';
    pushLog(s, logEntryHtml(reqMs, d, tag));
  }

  const burstSummary = count > 1
    ? { total: count, allowed: burstAllowed, denied: burstDenied }
    : null;

  renderState(id, lastDecision, reqMs, burstSummary);

  if (CONVEYOR_IDS.includes(id)) {
    queueVizDrops(id, dropMeta);
    updateConveyorVisuals();
  } else if (id === QUEUE_ID || id === COUNTER_ID) {
    queueVizDrops(id, dropMeta);
    updateSpecialVisuals();
  } else if (id === 'token') {
    updateBucketVisuals(false);
    const viz = document.getElementById('viz-token');
    viz?.classList.remove('token-spend');
    void viz?.offsetWidth;
    viz?.classList.add('token-spend');
    setTimeout(() => viz?.classList.remove('token-spend'), 400);
  } else if (id === 'leaky') {
    updateBucketVisuals(true);
  }
}

document.getElementById('btn-reset')?.addEventListener('click', init);

document.querySelectorAll('[data-speed]').forEach((btn) => {
  btn.addEventListener('click', () => setLabSpeed(Number(btn.dataset.speed)));
});

init();
setInterval(tickClock, 50);
