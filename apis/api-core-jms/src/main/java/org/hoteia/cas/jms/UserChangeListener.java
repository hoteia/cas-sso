package org.hoteia.cas.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hoteia.cas.dao.UserAuthenticationInfoRepository;
import org.hoteia.cas.model.UserAuthenticationInfo;
import org.hoteia.cas.pojo.UserCredentialsPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;

@Component("userChangeListener")
public class UserChangeListener implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private @Autowired UserAuthenticationInfoRepository repository;

    @Override
    public void onMessage(Message message) {
        Assert.isInstanceOf(TextMessage.class, message, "Only text messages are allowed");

        try {
            String messageText = readMessageText((TextMessage) message);
            UserCredentialsPojo updateInfo = parseMessageText(messageText);
            updateUserInformation(updateInfo);
        } catch (Exception e) {
            logger.error("Encountered unexpected exception", e);
            throw new RuntimeException(e);
        }
    }

    private void updateUserInformation(UserCredentialsPojo updateInfo) {
        UserAuthenticationInfo updatedInformation = prepareUsernameInformationForUpdate(updateInfo);
        repository.save(updatedInformation);
    }

    private UserAuthenticationInfo prepareUsernameInformationForUpdate(UserCredentialsPojo updateInfo) {
        UserAuthenticationInfo info = repository.findByUsername(updateInfo.getUsername());
        info = userDoesNotExist(info) ? new UserAuthenticationInfo() : info;
        BeanUtils.copyProperties(updateInfo, info, new String[]{"id"});
        return info;
    }

    private boolean userDoesNotExist(UserAuthenticationInfo info) {
        return info == null;
    }

    private UserCredentialsPojo parseMessageText(String messageText) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(messageText, UserCredentialsPojo.class);
    }

    private String readMessageText(TextMessage message) throws JMSException {
        String messageText = message.getText();
        logger.info("Received message {} with correlation Id {}", messageText, message.getJMSCorrelationID());
        return messageText;
    }
}
