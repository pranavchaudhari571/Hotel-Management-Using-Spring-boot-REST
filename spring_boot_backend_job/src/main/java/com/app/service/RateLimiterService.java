package com.app.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final int REQUEST_LIMIT = 5; // Max requests allowed per endpoint
    private final long TIME_WINDOW = 60; // Time window in seconds

    public boolean isAllowed(String clientIp, String apiPath) {
        String redisKey = "rate_limit:" + clientIp + ":" + apiPath; // Key includes API path

        Long requestCount = redisTemplate.opsForValue().increment(redisKey, 1);
        if (requestCount == 1) {
            redisTemplate.expire(redisKey, TIME_WINDOW, TimeUnit.SECONDS);
        }

        return requestCount <= REQUEST_LIMIT;
    }
}

