package org.hoteia.cas.security;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import org.jasig.cas.authentication.AuthenticationException;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;

public class SilentAuthenticationExceptionHandler {

    /** State name when no matching exception is found. */
    private static final String UNKNOWN = "UNKNOWN";

    /** Default message bundle prefix. */
    private static final String DEFAULT_MESSAGE_BUNDLE_PREFIX = "authenticationFailure.";

    /** Default list of errors this class knows how to handle. */
    private static final List<Class<? extends Exception>> DEFAULT_ERROR_LIST =
            new ArrayList<Class<? extends Exception>>();

    static {
        DEFAULT_ERROR_LIST.add(javax.security.auth.login.AccountLockedException.class);
        DEFAULT_ERROR_LIST.add(javax.security.auth.login.FailedLoginException.class);
        DEFAULT_ERROR_LIST.add(javax.security.auth.login.CredentialExpiredException.class);
        DEFAULT_ERROR_LIST.add(org.jasig.cas.authentication.AccountDisabledException.class);
        DEFAULT_ERROR_LIST.add(org.jasig.cas.authentication.InvalidLoginLocationException.class);
        DEFAULT_ERROR_LIST.add(org.jasig.cas.authentication.InvalidLoginTimeException.class);
    }

    /** Ordered list of error classes that this class knows how to handle. */
    @NotNull
    private List<Class<? extends Exception>> errors = DEFAULT_ERROR_LIST;

    /** String appended to exception class name to create a message bundle key for that particular error. */
    private String messageBundlePrefix = DEFAULT_MESSAGE_BUNDLE_PREFIX;

    /**
     * Sets the list of errors that this class knows how to handle.
     *
     * @param errors List of errors in order of descending precedence.
     */
    public void setErrors(final List<Class<? extends Exception>> errors) {
        this.errors = errors;
    }

    /**
     * Sets the message bundle prefix appended to exception class names to create a message bundle key for that
     * particular error.
     *
     * @param prefix Prefix appended to exception names.
     */
    public void setMessageBundlePrefix(final String prefix) {
        this.messageBundlePrefix = prefix;
    }

    /**
     * Maps an authentication exception onto a state name equal to the simple class name of the
     * {@link org.jasig.cas.authentication.AuthenticationException#getHandlerErrors()} with highest precedence.
     * Also sets an ERROR severity message in the message context of the form
     * <code>[messageBundlePrefix][exceptionClassSimpleName]</code> for each handler error.
     *
     * @param e Authentication error to handle.
     *
     * @return Name of next flow state to transition to or "none" if
     */
    public String handle(final AuthenticationException e, final MessageContext messageContext) {
        if (e != null) {
            String messageCode;
            for (final Exception handlerError : e.getHandlerErrors().values()) {
                messageCode = this.messageBundlePrefix + handlerError.getClass().getSimpleName();
                try {
                    final MessageResolver build = new MessageBuilder().error().code(messageCode).build();
                    messageContext.addMessage(build);
                } catch (Exception ignored) {
                    messageContext.addMessage(new MessageBuilder().error().code(this.messageBundlePrefix + UNKNOWN).build());
                }
            }
            for (final Class<? extends Exception> kind: this.errors) {
                for (final Exception handlerError : e.getHandlerErrors().values()) {
                    if (handlerError != null && handlerError.getClass().equals(kind)) {
                        return handlerError.getClass().getSimpleName();
                    }
                }

            }
        }
        messageContext.addMessage(new MessageBuilder().error().code(this.messageBundlePrefix + UNKNOWN).build());
        return UNKNOWN;
    }
}
