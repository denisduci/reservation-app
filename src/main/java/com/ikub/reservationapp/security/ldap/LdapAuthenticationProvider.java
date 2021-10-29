package com.ikub.reservationapp.security.ldap;

import com.ikub.reservationapp.security.ldap.config.LdapPasswordEncoder;
import com.ikub.reservationapp.security.ldap.service.LdapAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
    @Autowired
    private LdapPasswordEncoder ldapPasswordEncoder;

//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        log.info("trying to authenticate with ldap: -> {}", authentication);
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//        DirContextOperations userFromLdapContext = null;
//        String rolesAsStringFromLdap;
//        String userPasswordFromLdap;
//        //Filter filter = new EqualsFilter("uid", authentication.getName());
//        SpringSecurityLdapTemplate securityLdapTemplate = new SpringSecurityLdapTemplate(ldapTemplate.getContextSource());
//        String dn = "uid=" + authentication.getName() + ",ou=people";
//
//        try {
//            userFromLdapContext = securityLdapTemplate.retrieveEntry(dn, null);
//        } catch (NameNotFoundException e) {
//
//        }
//        log.info("User from ldap context: -> {}", userFromLdapContext);
//        if (userFromLdapContext != null) {
//            Attribute passwordAttribute = userFromLdapContext.getAttributes().get("userpassword");
//            Attribute rolesAttribute = userFromLdapContext.getAttributes().get("roles");
//
//            try {
//                userPasswordFromLdap = new String((byte[]) passwordAttribute.get());
//                rolesAsStringFromLdap = (String) rolesAttribute.get();
//            } catch (Exception e) {
//                log.error("Error while retrieving password and roles");
//                throw new ReservationApp500Exception("Something wrong happened!");
//            }
//
//            val isPasswordMatch = bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), userPasswordFromLdap);
//            //Boolean authenticate = ldapTemplate.authenticate(LdapUtils.emptyLdapName(), filter.encode(), authentication.getCredentials().toString());
//            if (isPasswordMatch) {
//                val userAuthoritiesFromLdap = ldapAuthenticationService.getGrantedAuthoritiesFromLdap(rolesAsStringFromLdap);
//                UserDetails userDetails = new User(authentication.getName(), authentication.getCredentials().toString()
//                        , userAuthoritiesFromLdap);
//                Authentication authWithAuthorities = new UsernamePasswordAuthenticationToken(userDetails,
//                        authentication.getCredentials().toString(), userAuthoritiesFromLdap);
//                SecurityContextHolder.getContext().setAuthentication(authWithAuthorities);
//                return authWithAuthorities;
//            }
//        }
//        return null;
//    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Trying to authenticate with ldap: -> {}", authentication);
        String rolesAsStringFromLdap;
        String userPasswordFromLdap;
        //Filter filter = new EqualsFilter("uid", authentication.getName());
        val userFromLdap = ldapAuthenticationService.getUserByUsername(authentication.getName());
        log.info("User from ldap: -> {}", userFromLdap);

        if (userFromLdap != null) {
            userPasswordFromLdap = userFromLdap.getPassword();
            rolesAsStringFromLdap = userFromLdap.getRoles();
            val isPasswordMatch = ldapPasswordEncoder.matches(authentication.getCredentials().toString(), userPasswordFromLdap);
            //Boolean authenticate = ldapTemplate.authenticate(LdapUtils.emptyLdapName(), filter.encode(), authentication.getCredentials().toString());
            if (isPasswordMatch) {
                val userAuthoritiesFromLdap = ldapAuthenticationService.getGrantedAuthoritiesFromLdap(rolesAsStringFromLdap);
                UserDetails userDetails = new User(authentication.getName(), authentication.getCredentials().toString()
                        , userAuthoritiesFromLdap);
                Authentication authWithAuthorities = new UsernamePasswordAuthenticationToken(userDetails,
                        authentication.getCredentials().toString(), userAuthoritiesFromLdap);
                SecurityContextHolder.getContext().setAuthentication(authWithAuthorities);
                log.info("Authenticated -> {}", authentication.getName());
                return authWithAuthorities;
            }
        }
        log.warn("No user found in ldap...");
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
