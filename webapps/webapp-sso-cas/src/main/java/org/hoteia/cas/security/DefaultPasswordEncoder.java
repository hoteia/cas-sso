package org.hoteia.cas.security;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

public class DefaultPasswordEncoder implements org.jasig.cas.authentication.handler.PasswordEncoder {

    private final PasswordEncoder encoder = new StandardPasswordEncoder();

    @Override
    public String encode(String password) {
        return encoder.encode(password);
    }
}
