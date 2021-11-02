package com.ikub.reservationapp.security.ldap.service;

import com.ikub.reservationapp.security.ldap.domain.UserLdap;
import com.ikub.reservationapp.security.ldap.dto.UserLdapResponseDto;
import com.ikub.reservationapp.security.ldap.dto.UserMergedResponseDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;

public interface LdapAuthenticationService {

    UserDetails loadUserDetailsFromLdap(String username);
    Set<SimpleGrantedAuthority> getGrantedAuthoritiesFromLdap(String rolesAsString);
    UserLdapResponseDto createLdapUser(UserLdap userLdap);
    UserLdap getUserByUsername(String username);
    List<UserLdapResponseDto> getAllLdapUsers();
    List<String> search(String username);
    List<UserMergedResponseDto> getMergedUsers();
}
