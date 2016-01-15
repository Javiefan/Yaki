package com.javie.yaki.rest;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Component
@Path("test")
public class TestResource {

    @GET
    public String hello() {
        return "hello world!";
    }
}
