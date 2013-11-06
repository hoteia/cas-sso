package com.fingolfintek.jms;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.hoteia.cas.dao.UserAuthenticationInfoRepository;
import org.hoteia.cas.jms.UserChangeListener;
import org.hoteia.cas.model.UserAuthenticationInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserChangeListenerTest {

    public static final String TEXT = "{\"username\":\"user\",\"password\":\"pass\"}";
    public static final String USER = "user";
    public static final String PASS = "pass";

    @Mock private UserAuthenticationInfoRepository repository;
    @InjectMocks  private UserChangeListener changeListener = new UserChangeListener();

    @Test
    public void testOnMessageWhenNewUser() throws Exception {
        TextMessage message = mockMessageToReturnDefaultText();

        changeListener.onMessage(message);

        verify(repository).save(defaultUserAuthenticationInfo());
    }

    private TextMessage mockMessageToReturnDefaultText() throws JMSException {
        return mockMessageToReturnText(TEXT);
    }

    private TextMessage mockMessageToReturnText(String text) throws JMSException {
        TextMessage message = mock(TextMessage.class);
        when(message.getText()).thenReturn(text);
        return message;
    }

    private UserAuthenticationInfo defaultUserAuthenticationInfo() {
        return createUserAuthenticationInfo(null, USER, PASS);
    }

    private UserAuthenticationInfo createUserAuthenticationInfo(Long id, String user, String pass) {
        UserAuthenticationInfo info = new UserAuthenticationInfo();
        info.setId(id);
        info.setUsername(user);
        info.setPassword(pass);
        return info;
    }

    @Test
    public void testOnMessageWhenExistingUser() throws Exception {
        UserAuthenticationInfo existingUser = createUserAuthenticationInfo(1L, "A", "B");
        TextMessage message = mockMessageToReturnDefaultText();
        when(repository.findByUsername(USER)).thenReturn(existingUser);

        changeListener.onMessage(message);

        verify(repository).save(existingUser);
        assertThat(existingUser).isEqualTo(createUserAuthenticationInfo(1L, USER, PASS));
    }

    @Test
    public void testOnMessageWithNonTextMessage() {
        try {
            changeListener.onMessage(mock(Message.class));
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch(IllegalArgumentException e) {
            verifyZeroInteractions(repository);
        }
    }

    @Test
    public void testOnMessageWithEmptyTextMessage() throws JMSException {
        try {
            TextMessage emptyMessage = mockMessageToReturnText("");
            changeListener.onMessage(emptyMessage);
            failBecauseExceptionWasNotThrown(JsonMappingException.class);
        } catch(RuntimeException e) {
            assertThatRuntimeExceptionWasCausedByJsonMappingException(e);
            verifyZeroInteractions(repository);
        }
    }

    private void assertThatRuntimeExceptionWasCausedByJsonMappingException(RuntimeException e) {
        assertThat(e).hasMessageContaining("No content to map due to end-of-input");
        assertThat(e.getCause()).isExactlyInstanceOf(JsonMappingException.class);
    }
}
