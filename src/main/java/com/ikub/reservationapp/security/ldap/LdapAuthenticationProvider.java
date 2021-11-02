package com.ikub.reservationapp.security.ldap;

import com.ikub.reservationapp.security.ldap.service.LdapAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component(value = "ldapAuthenticationProvider")
public class LdapAuthenticationProvider implements AuthenticationProvider {

    @Resource(name = "ldapAuthService")
    private LdapAuthenticationService ldapAuthenticationService;
    @Autowired
    private SpringSecurityLdapTemplate ldapTemplate;
    @Resource(name = "ldap")
    private PasswordEncoder ldapPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Trying to authenticate with ldap: -> {}", authentication);
        Filter filter = new EqualsFilter("uid", authentication.getName());
        Boolean authenticate = ldapTemplate.authenticate("", filter.encode(), ldapPasswordEncoder.encode(authentication.getCredentials().toString()));
        if (authenticate) {
            val ldapUserDetails = ldapAuthenticationService.loadUserDetailsFromLdap(authentication.getName());
            Authentication authWithAuthorities = new UsernamePasswordAuthenticationToken(ldapUserDetails,
                    authentication.getCredentials().toString(), ldapUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authWithAuthorities);
            return authWithAuthorities;
        }
        log.warn("No user found in ldap...");
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
