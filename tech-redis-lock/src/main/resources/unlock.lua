local locKKey = KEYS[1]
local lockVal = KEYS[2]

-- get key 
local result_1 = redis.call('get', locKKey)
if result_1 == lockVal
then
    local result_2 = redis.call('del', locKKey)
    return result_2
    
else
    return false
end 