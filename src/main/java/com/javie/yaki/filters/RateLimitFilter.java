package com.javie.yaki.filters;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.javie.yaki.annotation.RateLimited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * Created by Javie on 16/6/17.
 */
@RateLimited
public class RateLimitFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitFilter.class);

    //    @Autowired
    //    private StringRedisTemplate redisTemplate;

    @Context
    private HttpServletRequest requestContext;

    private static final String RATE_LIMIT = "counter:available";
    private static final String TIME_STAMP = "counter:ts";

    private static final int TIME_OUT = 10;
    private static final float RATE = 1;
    private static final float BUCKET = 100;
    private static final float TOKENS = 5;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        // check if client exceeded rate limit
        if (isOverRateLimit()) {
            containerRequestContext.abortWith(Response.status(429).entity("Too Many Requests").build());
        }
    }


    private boolean isOverRateLimit() {
        Jedis jedis = new Jedis("localhost");
        long currentTime = System.currentTimeMillis();
        String oldTokens = jedis.get(RATE_LIMIT);
        long oldTime;
        float currentTokens;
        LOGGER.info("=================== sCounter : " + oldTokens);
        if (oldTokens == null) {
            currentTokens = BUCKET;
        } else {
            oldTime = Long.parseLong(jedis.get(TIME_STAMP));
            LOGGER.info("++++++++++++++ time : " + (currentTime - oldTime) / 1000.0);
            currentTokens = Float.parseFloat(oldTokens) + Math.min((currentTime - oldTime) * RATE / 1000,
                                                                   BUCKET - Float.parseFloat(oldTokens));
        }

        boolean isOverRateLimit;
        if (TOKENS <= currentTokens) {
            currentTokens -= TOKENS;
            isOverRateLimit = false;
        } else {
            isOverRateLimit = true;
        }

        Transaction transaction = jedis.multi();
        transaction.set(RATE_LIMIT, currentTokens + "");
        transaction.expire(RATE_LIMIT, TIME_OUT);
        transaction.set(TIME_STAMP, currentTime + "");
        transaction.expire(TIME_STAMP, TIME_OUT);
        transaction.exec();

        return isOverRateLimit;
    }
    //    private boolean isOverRateLimit() {
    //        LOGGER.info("filter works!!!!!!");
    //        LOGGER.info("user: " + requestContext.getRemoteUser());
    //        LOGGER.info("ip: " + requestContext.getRemoteAddr());
    //
    //        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    //        Resource resource = resourcePatternResolver.getResource("classpath:SlidingWindowRateLimit.lua");
    ////        Resource resource = resourcePatternResolver.getResource("classpath:PythonRateLimit.py");
    //        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
    //        script.setLocation(resource);
    //        script.setResultType(Long.class);
    //
    //        ArrayList<String> keys = new ArrayList<>();
    //        keys.add(requestContext.getRemoteAddr());
    //        String[] params = new String[3];
    //        params[0] = "[[1, 10], [3, 200], [36, 3000]]";
    //        Long unixTime = System.currentTimeMillis() / 1000L;
    //        params[1] = (unixTime.toString());
    //        params[2] = "1";
    //
    //
    //        long result = redisTemplate.execute(script, keys, params);
    //
    //        return result != 0;
    //    }
}
