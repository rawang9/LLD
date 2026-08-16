-- Atomic fixed-window rate limiter (matches Java ratelimit.strategy.FixedWindow).
--
-- KEYS[1]  key (e.g. ratelimit:user:123)
-- ARGV[1]  limit         — tokens per window
-- ARGV[2]  window_ms     — window length in milliseconds
-- ARGV[3]  now_ms        — optional epoch ms (tests); default Redis TIME
--
-- Returns: { allowed, remaining, retry_at_ms }
--   retry_at_ms = window end when denied; -1 when allowed
-- TTL: 2 windows so idle keys vanish; active window is never dropped early.

local key = KEYS[1]
local limit = tonumber(ARGV[1])
local window_ms = tonumber(ARGV[2])

local now_ms
if ARGV[3] ~= nil and ARGV[3] ~= '' then
  now_ms = tonumber(ARGV[3])
else
  local t = redis.call('TIME')
  now_ms = tonumber(t[1]) * 1000 + math.floor(tonumber(t[2]) / 1000)
end

local window_start = tonumber(redis.call('HGET', key, 'window_start_ms'))
local tokens = tonumber(redis.call('HGET', key, 'tokens'))

if window_start == nil then
  window_start = now_ms
  tokens = limit
end

local window_end = window_start + window_ms
if now_ms >= window_end then
  local windows_passed = math.floor((now_ms - window_start) / window_ms)
  if windows_passed < 1 then
    windows_passed = 1
  end
  window_start = window_start + windows_passed * window_ms
  window_end = window_start + window_ms
  tokens = limit
end

local allowed
local remaining
local retry_at_ms

if tokens > 0 then
  tokens = tokens - 1
  allowed = 1
  remaining = tokens
  retry_at_ms = -1
else
  allowed = 0
  remaining = 0
  retry_at_ms = window_end
end

redis.call('HSET', key, 'window_start_ms', window_start, 'tokens', tokens)
-- Live until this window ends, plus one extra window of slack.
local ttl_ms = window_end - now_ms + window_ms
if ttl_ms < window_ms then
  ttl_ms = window_ms
end
redis.call('PEXPIRE', key, math.floor(ttl_ms))

return { allowed, remaining, retry_at_ms }
