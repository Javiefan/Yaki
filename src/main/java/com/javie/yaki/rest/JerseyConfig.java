package com.javie.yaki.rest;

import com.javie.yaki.filters.RateLimitFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig{

    public JerseyConfig() {
        register(TestResource.class);
        register(RateLimitFilter.class);
    }
}
