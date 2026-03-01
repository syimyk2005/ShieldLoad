local key = KEYS[1]
local window = tonumber(ARGV[1])
local limit = tonumber(ARGV[2])

local current = tonumber(redis.call('GET', key) or '0')
local ttl = redis.call('TTL', key)

local remaining = math.max(limit - current, 0)

if ttl < 0 then
    ttl = window
end

return {current, remaining, ttl}