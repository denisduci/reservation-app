//package com.ikub.reservationapp.security.ldap;
//
//import com.ikub.reservationapp.common.enums.Role;
//import com.ikub.reservationapp.security.ldap.service.LdapAuthenticationService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import lombok.val;
//import org.springframework.ldap.core.DirContextOperations;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.HashSet;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class LdapUserAuthoritiesPopulator implements LdapAuthoritiesPopulator {
//
////    @Resource(name = "userService")
////    private UserDetailsService userDetailsService;
//
//    private LdapAuthenticationService ldapAuthenticationService;
//
//    @Override
//    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
//        Collection<? extends GrantedAuthority> authorities = new HashSet<>();
//        try {
//           // authorities = userDetailsService.loadUserByUsername(username).getAuthorities();
//            val data = ldapAuthenticationService.getUserByUsername(username);
//            authorities = ldapAuthenticationService.getGrantedAuthoritiesFromLdap(data.getRoles());
//
//        } catch (Exception e) {
//            log.warn("Unable to fetch the user authorities from the database. Hence, assigning default user role");
//            authorities = new ArrayList<>();
//        }
//        return authorities;
//    }
//}