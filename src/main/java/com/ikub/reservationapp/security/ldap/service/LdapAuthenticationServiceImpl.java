package com.ikub.reservationapp.security.ldap.service;

import com.ikub.reservationapp.common.exception.BadRequest;
import com.ikub.reservationapp.common.exception.ReservationApp500Exception;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.security.ldap.domain.UserLdap;
import com.ikub.reservationapp.security.ldap.dto.UserLdapResponseDto;
import com.ikub.reservationapp.security.ldap.dto.UserMergedResponseDto;
import com.ikub.reservationapp.security.ldap.mappers.LdapUserMapper;
import com.ikub.reservationapp.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.userdetails.*;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Slf4j
@Service(value = "ldapAuthService")
public class LdapAuthenticationServiceImpl implements LdapAuthenticationService {

    @Resource(name = "userService")
    private UserService userService;
    @Autowired
    private SpringSecurityLdapTemplate securityLdapTemplate;
    @Autowired
    private LdapUserMapper ldapUserMapper;
    @Resource(name = "ldap")
    private PasswordEncoder ldapPasswordEncoder;

    @Override
    public UserDetails loadUserDetailsFromLdap(String username) {
        String rolesAsStringFromLdap;
        val userData = securityLdapTemplate.retrieveEntry("uid=" + username + ",ou=people", null);
        if (userData != null) {
            rolesAsStringFromLdap = userData.getStringAttribute("roles");
            UserDetailsContextMapper userDetailsMapper = new LdapUserDetailsMapper();
            val ldapUserDetails = userDetailsMapper.mapUserFromContext(userData, username,
                    getGrantedAuthoritiesFromLdap(rolesAsStringFromLdap));
            log.info("LDAP User Details are: -> {}", ldapUserDetails);
            return ldapUserDetails;
        }
        log.warn("No user details found...");
        return null;
    }

    @Override
    public Set<SimpleGrantedAuthority> getGrantedAuthoritiesFromLdap(String rolesAsStringFromLdap) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        List<String> rolesAsListFromLdap = new ArrayList<>(Arrays.asList(rolesAsStringFromLdap.split(",")));
        rolesAsListFromLdap.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        });
        log.info("User Ldap authorities are: -> {}", authorities);
        return authorities;
    }

    @Override
    public UserLdapResponseDto createLdapUser(UserLdap userLdap) {
        Name dn = LdapUtils.newLdapName("uid=" + userLdap.getUsername() + ",ou=people");
        log.info("dn for user to create is -> {}", dn);
        val ldapExistingUser = getUserByUsername(userLdap.getUsername());
        Optional.ofNullable(ldapExistingUser).ifPresent(user -> {
            log.error("User already exists");
            throw new ReservationAppException(BadRequest.USER_EXISTS.getMessage());
        });
        try {
            securityLdapTemplate.bind(dn, null, buildAttributes(userLdap));

        } catch (Exception e) {
            log.error("Error while saving user to ldap: -> {}", e);
            throw new ReservationApp500Exception("Error while saving user to ldap");
        }
        userLdap.setRoles("PATIENT");
        return ldapUserMapper.toResponseLdapUser(userLdap);
    }

    @Override
    public List<UserMergedResponseDto> getMergedUsers() {
        val dbUsers = userService.getAllUsersWithoutPagination();
        val dbMergedList = dbUsers.stream().map(ldapUserMapper::toMergedResponse)
                .collect(Collectors.toList());

        val ldapUsers = getAllLdapUsers();
        val ldapMergedList = ldapUsers.stream().map(ldapUserMapper::toMergedResponse)
                .collect(Collectors.toList());

        List<UserMergedResponseDto> finalMergedListOfUsers = Stream.of(dbMergedList, ldapMergedList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return finalMergedListOfUsers;
    }

    @Override
    public List<UserLdapResponseDto> getAllLdapUsers() {
        val ldapUsers = securityLdapTemplate.search(query()
                .where("objectclass").is("person"), new UserAttributesMapper());
        return ldapUsers.stream().map(ldapUserMapper::toResponseLdapUser)
                .collect(Collectors.toList());
    }

    @Override
    public UserLdap getUserByUsername(String username) {
        UserLdap userLdap = null;
        Name dn = LdapUtils.newLdapName("uid=" + username + ",ou=people");
        try {
            userLdap = securityLdapTemplate.lookup(dn, new UserAttributesMapper());
        } catch (NameNotFoundException e) {

        }
        return userLdap;
    }

    private class UserAttributesMapper implements AttributesMapper<UserLdap> {
        public UserLdap mapFromAttributes(Attributes attrs) throws NamingException {
            UserLdap person = new UserLdap();
            person.setFirstName((String) attrs.get("cn").get());
            person.setLastName((String) attrs.get("sn").get());
            person.setRoles((String) attrs.get("roles").get());
            person.setUsername((String) attrs.get("uid").get());
            person.setPassword(new String((byte[]) attrs.get("userPassword").get()));
            person.setEmail((String) attrs.get("email").get());
            person.setPhone((String) attrs.get("phone").get());
            return person;
        }
    }

    private Attributes buildAttributes(UserLdap userLdap) {
        Attributes attrs = new BasicAttributes();
        BasicAttribute ocattr = new BasicAttribute("objectclass");
        ocattr.add("top");
        ocattr.add("person");
        ocattr.add("organizationalPerson");
        ocattr.add("inetOrgPerson");
        attrs.put(ocattr);
        attrs.put("cn", userLdap.getFirstName() + " " + userLdap.getLastName());
        attrs.put("sn", userLdap.getLastName());
        attrs.put("uid", userLdap.getUsername());
        attrs.put("email", userLdap.getEmail());
        attrs.put("phone", userLdap.getPhone());
        attrs.put("userPassword", ldapPasswordEncoder.encode(userLdap.getPassword()));
        attrs.put("roles", "PATIENT");
        return attrs;
    }

    private String digestSHA(final String password) {
        String base64;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA");
            digest.update(password.getBytes());
            base64 = Base64.getEncoder()
                    .encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return "{SHA}" + base64;
    }

    @Override
    public List<String> search(String username) {
        val response = securityLdapTemplate.search(
                "ou=people",
                "uid=" + username,
                (AttributesMapper<String>) attrs -> (String) attrs
                        .get("uid")
                        .get());

        val response2 = securityLdapTemplate.search(
                query().where("objectclass").is("person"),
                (AttributesMapper<String>) attributes -> (String) attributes.get("uid").get());

        return response;
    }
}
