package org.hoteia.cas.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

@Component("jmsExceptionListener")
public class JmsExceptionListener implements ExceptionListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onException(JMSException e) {
        logger.error("Unexpected error while processing JMS message");
    }
}
