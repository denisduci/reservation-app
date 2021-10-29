package com.ikub.reservationapp.security.ldap.mappers;

import com.ikub.reservationapp.security.ldap.domain.UserLdap;
import com.ikub.reservationapp.security.ldap.dto.UserLdapResponseDto;
import com.ikub.reservationapp.security.ldap.dto.UserMergedResponseDto;
import com.ikub.reservationapp.users.dto.UserResponseDto;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.stereotype.Component;

@Component

public class LdapUserMapper {
    public UserLdapResponseDto toResponseLdapUser(UserLdap userLdap) {
        UserLdapResponseDto responseUser = new UserLdapResponseDto();
        if (userLdap == null)
            return null;
        responseUser.setEmail(userLdap.getEmail());
        responseUser.setFirstName(userLdap.getFirstName());
        responseUser.setLastName(userLdap.getLastName());
        responseUser.setPhone(userLdap.getPhone());
        responseUser.setRoles(userLdap.getRoles());
        return responseUser;
    }


    public UserMergedResponseDto toMergedResponse(Object o) {
        UserMergedResponseDto userMergedResponseDto = new UserMergedResponseDto();
        if (o == null)
            return null;
        if (o instanceof UserResponseDto) {
            UserResponseDto dbUserToMap = (UserResponseDto) o;
            userMergedResponseDto.setEmail(dbUserToMap.getEmail());
            userMergedResponseDto.setFirstName(dbUserToMap.getFirstName());
            userMergedResponseDto.setLastName(dbUserToMap.getLastName());
            userMergedResponseDto.setPhone(dbUserToMap.getPhone());
            userMergedResponseDto.setRoles(dbUserToMap.getRoleName());
            userMergedResponseDto.setType("DB_USER");
        }
        if (o instanceof UserLdapResponseDto) {
            UserLdapResponseDto ldapUserToMap = (UserLdapResponseDto) o;
            userMergedResponseDto.setEmail(ldapUserToMap.getEmail());
            userMergedResponseDto.setFirstName(ldapUserToMap.getFirstName());
            userMergedResponseDto.setLastName(ldapUserToMap.getLastName());
            userMergedResponseDto.setPhone(ldapUserToMap.getPhone());
            userMergedResponseDto.setRoles(ldapUserToMap.getRoles());
            userMergedResponseDto.setType("LDAP_USER");
        }
        return userMergedResponseDto;
    }
}
