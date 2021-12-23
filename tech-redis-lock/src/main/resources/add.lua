local lockKey=KEYS[1]
local lockVal=KEYS[2]

--setnx info
local result_1 = redis.call('SETNX',lockKey,lockVal)
if result_1 == true
then
    local result_2 = redis.call('SETEX',lockKey,3600,lockVal)
    return result_1
else
    return result_1
end 