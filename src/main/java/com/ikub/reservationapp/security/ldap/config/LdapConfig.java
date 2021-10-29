package com.ikub.reservationapp.security.ldap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;

@Configuration
public class LdapConfig {

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://localhost:8389/dc=springframework,dc=org");
        //contextSource.setAnonymousReadOnly(true);
        contextSource.setUserDn("uid=admin,ou=people,dc=springframework,dc=org");
        contextSource.setPassword("$2a$12$2O0gxW3ssM6/jJHyzMZZweR4pCKUHNwjCcsiqBqk/Vcc9cWILoR7C");
        //contextSource.setBase("ou=groups");
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Bean
    public SpringSecurityLdapTemplate ldapTemplate() {
        return new SpringSecurityLdapTemplate(contextSource());
    }

    @Bean
    public LdapPasswordEncoder passwordEncoder() {
        return new LdapPasswordEncoder();
    }

}