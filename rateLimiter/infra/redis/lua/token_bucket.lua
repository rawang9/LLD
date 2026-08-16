-- Atomic token-bucket rate limiter (matches Java ratelimit.strategy.TokenBucket).
--
-- KEYS[1]  bucket key (e.g. ratelimit:cto:123)
-- ARGV[1]  capacity      — max tokens (Java: limit)
-- ARGV[2]  window_ms     — refill window in milliseconds (Java: window)
-- ARGV[3]  refill_rate   — tokens earned per window (Java: refillRate)
-- ARGV[4]  now_ms        — optional request time in epoch ms (for tests; defaults to Redis TIME)
--
-- Returns: { allowed, remaining, retry_at_ms }
--   allowed      — 1 allow, 0 deny
--   remaining    — tokens left after this request
--   retry_at_ms  — epoch ms when next token is due; -1 when allowed
-- TTL: time to refill from empty + one window so idle keys vanish.

local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local window_ms = tonumber(ARGV[2])
local refill_rate = tonumber(ARGV[3])

local now_ms
if ARGV[4] ~= nil and ARGV[4] ~= '' then
  now_ms = tonumber(ARGV[4])
else
  local t = redis.call('TIME')
  now_ms = tonumber(t[1]) * 1000 + math.floor(tonumber(t[2]) / 1000)
end

local ms_per_token = window_ms / refill_rate

local tokens = tonumber(redis.call('HGET', key, 'tokens'))
local last_refill_ms = tonumber(redis.call('HGET', key, 'last_refill_ms'))

if tokens == nil then
  tokens = capacity
  last_refill_ms = now_ms
end

local elapsed_ms = now_ms - last_refill_ms
if elapsed_ms > 0 then
  local produced = math.floor(elapsed_ms / ms_per_token)
  if produced > 0 then
    tokens = math.min(capacity, tokens + produced)
    last_refill_ms = last_refill_ms + produced * ms_per_token
  end
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
  retry_at_ms = last_refill_ms + ms_per_token
end

redis.call('HSET', key, 'tokens', tokens, 'last_refill_ms', last_refill_ms)
-- Idle keys die after a full refill from empty, plus one window of slack.
local ttl_ms = math.floor(capacity * ms_per_token + window_ms)
if ttl_ms < window_ms then
  ttl_ms = window_ms
end
redis.call('PEXPIRE', key, ttl_ms)

return { allowed, remaining, retry_at_ms }
