-- Atomic leaky-bucket rate limiter (matches Java ratelimit.strategy.LeakyBucket).
-- Water starts EMPTY. Time leaks water. A request adds 1 if under capacity.
--
-- KEYS[1]  key
-- ARGV[1]  capacity      — max water (Java: limit)
-- ARGV[2]  window_ms
-- ARGV[3]  leak_rate     — units leaked per window (Java: refillRate used as leak)
-- ARGV[4]  now_ms        — optional epoch ms (tests); default Redis TIME
--
-- Returns: { allowed, remaining, retry_at_ms }
--   remaining = free capacity after this request
--   retry_at_ms = last_leak + ms_per_leak when denied; -1 when allowed
-- TTL: time to drain a full bucket + one window so idle empty keys vanish.

local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local window_ms = tonumber(ARGV[2])
local leak_rate = tonumber(ARGV[3])

local now_ms
if ARGV[4] ~= nil and ARGV[4] ~= '' then
  now_ms = tonumber(ARGV[4])
else
  local t = redis.call('TIME')
  now_ms = tonumber(t[1]) * 1000 + math.floor(tonumber(t[2]) / 1000)
end

local ms_per_leak = window_ms / leak_rate

local water = tonumber(redis.call('HGET', key, 'water'))
local last_leak_ms = tonumber(redis.call('HGET', key, 'last_leak_ms'))

if water == nil then
  water = 0
  last_leak_ms = now_ms
end

local elapsed_ms = now_ms - last_leak_ms
if elapsed_ms > 0 then
  local leaked = math.floor(elapsed_ms / ms_per_leak)
  if leaked > 0 then
    -- Leak even when water is 0 so last_leak still advances (no idle burst +1).
    water = math.max(0, water - leaked)
    last_leak_ms = last_leak_ms + leaked * ms_per_leak
  end
end

local allowed
local remaining
local retry_at_ms

if water < capacity then
  water = water + 1
  allowed = 1
  remaining = capacity - water
  retry_at_ms = -1
else
  allowed = 0
  remaining = 0
  retry_at_ms = last_leak_ms + ms_per_leak
end

redis.call('HSET', key, 'water', water, 'last_leak_ms', last_leak_ms)
local ttl_ms = math.floor(capacity * ms_per_leak + window_ms)
if ttl_ms < window_ms then
  ttl_ms = window_ms
end
redis.call('PEXPIRE', key, ttl_ms)

return { allowed, remaining, retry_at_ms }
