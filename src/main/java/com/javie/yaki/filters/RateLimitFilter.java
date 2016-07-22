package com.javie.yaki.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.javie.yaki.annotation.RateLimited;
import com.javie.yaki.properties.RateLimitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * Created by Javie on 16/6/17.
 */

@RateLimited
@Provider
public class RateLimitFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitFilter.class);

    @Context
    private ResourceInfo info;

    @Autowired
    private RateLimitProperties rateLimitProperties;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        // check if client exceeded rate limit
        if (isOverRateLimit()) {
            containerRequestContext.abortWith(Response.status(429).entity("Too Many Requests").build());
        }
    }


    private boolean isOverRateLimit() {
        RateLimited rateLimited = info.getResourceMethod().getAnnotation(RateLimited.class);
        float costTokens = rateLimited.cost();
        String tokenKey = rateLimited.key().concat(":available");
        String timeKey = rateLimited.key().concat(":ts");
        int timeout = rateLimitProperties.getTimeout();
        float rate = rateLimitProperties.getRate();
        float bucket = rateLimitProperties.getBucket();

        Jedis jedis = new Jedis("localhost");
        long currentTime = System.currentTimeMillis();
        String oldTokens = jedis.get(tokenKey);
        long oldTime;
        float currentTokens;
        LOGGER.info("=================== sCounter : " + oldTokens);
        if (oldTokens == null) {
            currentTokens = bucket;
        } else {
            oldTime = Long.parseLong(jedis.get(timeKey));
            LOGGER.info("++++++++++++++ time : " + (currentTime - oldTime) / 1000.0);
            currentTokens = Float.parseFloat(oldTokens) + Math.min((currentTime - oldTime) * rate / 1000,
                                                                   bucket - Float.parseFloat(oldTokens));
        }

        boolean isOverRateLimit;
        if (costTokens <= currentTokens) {
            currentTokens -= costTokens;
            isOverRateLimit = false;
        } else {
            isOverRateLimit = true;
        }

        Transaction transaction = jedis.multi();
        transaction.set(tokenKey, currentTokens + "");
        transaction.expire(tokenKey, timeout);
        transaction.set(timeKey, currentTime + "");
        transaction.expire(timeKey, timeout);
        transaction.exec();

        return isOverRateLimit;
    }
}
