package org.hoteia.cas.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hoteia.cas.pojo.UserCredentialsPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;

public class UserService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Value("${jms.queue}")
    private String queue;

    @Autowired
    private JmsTemplate jmsTemplate;

    @PostConstruct
    protected void initialize() {
        accountRepository.save(new Account("casuser", "demo", "ROLE_USER"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("user not found");
        }
        return createUser(account);
    }

    public void signin(Account account) {
        SecurityContextHolder.getContext().setAuthentication(authenticate(account));
    }

    public void saveUser(Account account) {
        accountRepository.save(account);
        updateCas(account);
    }

    private void updateCas(Account account) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            final UserCredentialsPojo userCredentialsPojo = new UserCredentialsPojo(account.getEmail(), account.getPassword());
            jmsTemplate.convertAndSend(queue, mapper.writeValueAsString(userCredentialsPojo));
        } catch (IOException e) {
        }
    }

    private Authentication authenticate(Account account) {
        return new UsernamePasswordAuthenticationToken(createUser(account), null, Collections.singleton(createAuthority(account)));
    }

    private User createUser(Account account) {
        return new User(account.getEmail(), account.getPassword(), Collections.singleton(createAuthority(account)));
    }

    private GrantedAuthority createAuthority(Account account) {
        return new SimpleGrantedAuthority(account.getRole());
    }

}
