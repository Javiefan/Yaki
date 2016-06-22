package com.javie.yaki.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.javie.yaki.annotation.RateLimited;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * Created by Javie on 16/6/17.
 */
@RateLimited
public class RateLimitFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitFilter.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Context
    private HttpServletRequest requestContext;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        // check if client exceeded rate limit
        if (isOverRateLimit())
        {
            containerRequestContext.abortWith(Response.status(429).entity("Too Many Requests").build());
        }
    }

    private boolean isOverRateLimit() {
        LOGGER.info("filter works!!!!!!");
        LOGGER.info("user: " + requestContext.getRemoteUser());
        LOGGER.info("ip: " + requestContext.getRemoteAddr());

        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource resource = resourcePatternResolver.getResource("classpath:SlidingWindowRateLimit.lua");
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(resource);
        script.setResultType(Long.class);

        ArrayList<String> keys = new ArrayList<>();
        keys.add(requestContext.getRemoteAddr());
        String[] params = new String[3];
        params[0] = "[[1, 10], [3, 200], [36, 3000]]";
        Long unixTime = System.currentTimeMillis() / 1000L;
        params[1] = (unixTime.toString());
        params[2] = "1";

        long result = (long)redisTemplate.execute(script, keys, params);

        if (result != 0) {
            return true;
        } else {
            return false;
        }
    }
}
