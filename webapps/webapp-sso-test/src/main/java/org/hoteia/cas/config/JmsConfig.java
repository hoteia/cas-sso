package org.hoteia.cas.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Configuration
public class JmsConfig {

    @Value("${jms.url}")
    private String url;

    @Value("${jms.username}")
    private String username;

    @Value("${jms.password}")
    private String password;

    @Bean
    public ConnectionFactory amqConnectionFactory() {
        return new ActiveMQConnectionFactory(username, password, url);
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }
}
