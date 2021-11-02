package com.ikub.reservationapp.security.ldap.controller;

import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.security.ldap.domain.UserLdap;
import com.ikub.reservationapp.security.ldap.dto.UserLdapResponseDto;
import com.ikub.reservationapp.security.ldap.dto.UserMergedResponseDto;
import com.ikub.reservationapp.security.ldap.service.LdapAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ldap-auth")
public class LdapController {

    @Resource(name = "ldapAuthService")
    private LdapAuthenticationService ldapAuthenticationService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<UserLdapResponseDto> createLdapUser(@Valid @RequestBody UserLdap userLdap) throws ReservationAppException {
        log.info("Creating an ldap user...");
        return new ResponseEntity<>(ldapAuthenticationService.createLdapUser(userLdap), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserLdapResponseDto>> getAllLdapUsers()  {
        log.info("Retrieving all ldap users...");
        return new ResponseEntity<>(ldapAuthenticationService.getAllLdapUsers(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/merged")
    public ResponseEntity<List<UserMergedResponseDto>> getMergedUsers()  {
        log.info("Retrieving merged users of application...");
        return new ResponseEntity<>(ldapAuthenticationService.getMergedUsers(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/username")
    public ResponseEntity<UserLdap> getByUsername()  {
        log.info("Retrieving merged users of application...");
        return new ResponseEntity<>(ldapAuthenticationService.getUserByUsername("secretary"), HttpStatus.OK);
    }
}
