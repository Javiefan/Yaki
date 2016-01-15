package com.javie.yaki;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.stereotype.Component;

@Component
public class JettyCustomizer implements EmbeddedServletContainerCustomizer{

    private final int port;

    @Autowired
    public JettyCustomizer(@Value("${server.port}") int port) {
        this.port = port;
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        if (container instanceof JettyEmbeddedServletContainerFactory) {
            customizeJetty((JettyEmbeddedServletContainerFactory) container);
        }
    }

    public void customizeJetty(JettyEmbeddedServletContainerFactory jetty) {
        jetty.addServerCustomizers(new JettyServerCustomizer() {
            @Override
            public void customize(Server server) {
                ServerConnector connector = new ServerConnector(server);
                connector.setPort(port);
            }
        });
    }
}
