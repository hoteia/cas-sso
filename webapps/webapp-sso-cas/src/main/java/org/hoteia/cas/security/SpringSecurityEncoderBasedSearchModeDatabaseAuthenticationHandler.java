package org.hoteia.cas.security;

import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.handler.PasswordEncoder;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;

import javax.security.auth.login.FailedLoginException;
import javax.validation.constraints.NotNull;
import java.security.GeneralSecurityException;

public class SpringSecurityEncoderBasedSearchModeDatabaseAuthenticationHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler
        implements InitializingBean {

    private static final String SQL_TEMPLATE = "Select %s from %s where %s = ?";

    private String sql;

    @NotNull private String fieldUser;

    @NotNull private String fieldPassword;

    @NotNull private String tableUsers;

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Principal authenticateUsernamePasswordInternal(final String username, final String password)
            throws GeneralSecurityException, PreventedException {
        final String dbPassword = fetchPasswordFromDatabase(username);
        return verifyPassword(username, password, dbPassword);

    }

    private String fetchPasswordFromDatabase(String username) throws PreventedException {
        try {
            return getJdbcTemplate().queryForObject(sql, String.class, username);
        } catch (final DataAccessException e) {
            throw new PreventedException("SQL exception while executing query for " + username, e);
        }
    }

    private Principal verifyPassword(String username, String password, String dbPassword) throws FailedLoginException {
        if (passwordsMatch(password, dbPassword)) {
            return new SimplePrincipal(username);
        }

        throw new FailedLoginException(username + " not found with SQL query.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        sql = String.format(SQL_TEMPLATE, fieldPassword, tableUsers, fieldUser);
    }

    public final void setFieldPassword(final String fieldPassword) {
        this.fieldPassword = fieldPassword;
    }

    public final void setFieldUser(final String fieldUser) {
        this.fieldUser = fieldUser;
    }

    public final void setTableUsers(final String tableUsers) {
        this.tableUsers = tableUsers;
    }

    private boolean passwordsMatch(String plainPassword, String encodedPassword) {
        PasswordEncoder encoder = getPasswordEncoder();

        if (encoder instanceof SpringSecurityPasswordEncoder) {
            final SpringSecurityPasswordEncoder springSecurityPasswordEncoder = (SpringSecurityPasswordEncoder) encoder;
            return springSecurityPasswordEncoder.getEncoder().matches(plainPassword, encodedPassword);
        } else
            return encodedPassword.equals(encoder.encode(plainPassword));
    }
}
