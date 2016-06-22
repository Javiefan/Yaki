package com.javie.yaki.rest;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.javie.yaki.annotation.RateLimited;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@Path("test")
public class TestResource {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GET
    @RateLimited
    public String hello() {
        return "hello world!";
    }

    @POST
    @Path("redis")
    public Response save(@QueryParam("key") String key, @QueryParam("value") String value) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, value);
        return Response.ok().build();
    }

    @GET
    @RateLimited
    @Path("redis/{key}")
    public String get(@PathParam("key") String key) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(key);
    }

}
