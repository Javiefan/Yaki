package com.javie.yaki.rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.javie.yaki.annotation.RateLimited;
import org.springframework.stereotype.Component;

@Component
@Path("test")
public class TestResource {

    @GET
    @RateLimited(cost = 10, key = "test")
    public String hello() {
        return "hello world!";
    }

    //    @POST
    //    @Path("redis")
    //    public Response save(@QueryParam("key") String key, @QueryParam("value") String value) {
    //        ValueOperations<String, String> ops = redisTemplate.opsForValue();
    //        ops.set(key, value);
    //        return Response.ok().build();
    //    }
    //
    //    @GET
    //    @RateLimited
    //    @Path("redis/{key}")
    //    public String get(@PathParam("key") String key) {
    //        ValueOperations<String, String> ops = redisTemplate.opsForValue();
    //        return ops.get(key);
    //    }

}
