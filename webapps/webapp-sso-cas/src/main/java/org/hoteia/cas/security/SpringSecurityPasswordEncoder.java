package org.hoteia.cas.security;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

public class SpringSecurityPasswordEncoder implements org.jasig.cas.authentication.handler.PasswordEncoder {
    private final PasswordEncoder encoder;

    public SpringSecurityPasswordEncoder() {
        encoder = new StandardPasswordEncoder();
    }

    @Override
    public String encode(String password) {
        return encoder.encode(password);
    }

    public PasswordEncoder getEncoder() {
        return encoder;
    }
}
