-- Atomic sliding-window log (matches Java ratelimit.strategy.SlidingWindowLog).
--
-- KEYS[1]  key — Redis ZSET, score = request time ms
-- ARGV[1]  limit
-- ARGV[2]  window_ms
-- ARGV[3]  now_ms        — optional epoch ms (tests); default Redis TIME
--
-- Returns: { allowed, remaining, retry_at_ms }
--   retry_at_ms = oldest_in_window + window when denied; -1 when allowed
-- TTL: one window after last write — idle keys and stale stamps go away.

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

if limit <= 0 then
  return { 0, 0, -1 }
end

-- Window is (now - W, now]. Stamp exactly W ago is expired (ZREMRANGE inclusive).
local window_start = now_ms - window_ms
redis.call('ZREMRANGEBYSCORE', key, '-inf', window_start)

local n = redis.call('ZCARD', key)
local allowed
local remaining
local retry_at_ms

if n < limit then
  -- Member must be unique when many requests share the same ms.
  local member = tostring(now_ms) .. ':' .. tostring(n)
  redis.call('ZADD', key, now_ms, member)
  allowed = 1
  remaining = limit - n - 1
  retry_at_ms = -1
else
  local oldest = redis.call('ZRANGE', key, 0, 0, 'WITHSCORES')
  allowed = 0
  remaining = 0
  retry_at_ms = tonumber(oldest[2]) + window_ms
end

redis.call('PEXPIRE', key, math.floor(window_ms))

return { allowed, remaining, retry_at_ms }
