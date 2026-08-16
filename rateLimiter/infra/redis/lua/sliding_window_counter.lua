-- Atomic sliding-window counter (matches Java ratelimit.strategy.SlidingWindowCounter).
--
-- KEYS[1]  key
-- ARGV[1]  limit
-- ARGV[2]  window_ms
-- ARGV[3]  now_ms        — optional epoch ms (tests); default Redis TIME
--
-- Returns: { allowed, remaining, retry_at_ms }
--   retry_at_ms = current window end when denied (approximate); -1 when allowed
-- TTL: 2 windows — previous count is still needed for the weight.

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

local current_start = tonumber(redis.call('HGET', key, 'current_start_ms'))
local current_count = tonumber(redis.call('HGET', key, 'current_count'))
local prev_count = tonumber(redis.call('HGET', key, 'prev_count'))

if current_start == nil then
  current_start = now_ms
  current_count = 0
  prev_count = 0
end

local current_end = current_start + window_ms
if now_ms >= current_end then
  local windows_passed = math.floor((now_ms - current_start) / window_ms)
  if windows_passed < 1 then
    windows_passed = 1
  end
  if windows_passed == 1 then
    prev_count = current_count
  else
    prev_count = 0
  end
  current_start = current_start + windows_passed * window_ms
  current_count = 0
  current_end = current_start + window_ms
end

local elapsed = now_ms - current_start
local overlap = window_ms - elapsed
if overlap < 0 then
  overlap = 0
end
local weighted_prev = math.floor((overlap * prev_count) / window_ms)
local estimated = weighted_prev + current_count

local allowed
local remaining
local retry_at_ms

if estimated < limit then
  current_count = current_count + 1
  allowed = 1
  remaining = limit - estimated - 1
  retry_at_ms = -1
else
  allowed = 0
  remaining = 0
  retry_at_ms = current_end
end

redis.call('HSET', key,
  'current_start_ms', current_start,
  'current_count', current_count,
  'prev_count', prev_count)
redis.call('PEXPIRE', key, math.floor(2 * window_ms))

return { allowed, remaining, retry_at_ms }
