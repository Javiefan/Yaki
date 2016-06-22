package com.javie.yaki;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class BootStrap extends SpringBootServletInitializer{

    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BootStrap.class);
    }

    public static void main(String[] args) {
        LOGGER.info("Starting application!");
        new BootStrap()
                .configure(new SpringApplicationBuilder(BootStrap.class))
                .run(args);


    }
}
