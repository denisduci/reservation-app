package com.ikub.reservationapp.mongodb.mappers;

import com.ikub.reservationapp.mongodb.dto.UserMongoDto;
import com.ikub.reservationapp.mongodb.dto.UserMongoResponseDto;
import com.ikub.reservationapp.mongodb.model.UserMongo;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMongoMapper {

    @Autowired
    private RoleMapper roleMapper;

    public UserMongo toUserMongo(UserMongoDto userMongoDto) {
        UserMongo response = new UserMongo();
        if (userMongoDto == null)
            return null;
        if (userMongoDto.getFirstName() != null)
            response.setFirstName(userMongoDto.getFirstName());
        if (userMongoDto.getLastName() != null)
            response.setLastName(userMongoDto.getLastName());
        if (userMongoDto.getEmail() != null)
            response.setEmail(userMongoDto.getEmail());
        if (userMongoDto.getUsername() != null)
            response.setUsername(userMongoDto.getUsername());
        if (userMongoDto.getId() != null)
            response.setId(userMongoDto.getId());
        if (userMongoDto.getPassword() != null)
            response.setPassword(userMongoDto.getPassword());
        if (userMongoDto.getPhone() != null)
            response.setPhone(userMongoDto.getPhone());
        return response;
    }

    public UserMongoDto toUserMongoDto(UserMongo user) {
        UserMongoDto response = new UserMongoDto();
        if (user == null)
            return null;
        if (user.getEmail() != null)
            response.setEmail(user.getEmail());
        if (user.getUsername() != null)
            response.setUsername(user.getUsername());
        if (user.getId() != null)
            response.setId(user.getId());
        if (user.getPassword() != null)
            response.setPassword(user.getPassword());
        if (user.getFirstName() != null)
            response.setFirstName(user.getFirstName());
        if (user.getLastName() != null)
            response.setLastName(user.getLastName());
        if (user.getRoles() != null) {
            val roles = user.getRoles().stream().map(roleMapper::mapStringToRole)
                    .collect(Collectors.toSet());
            response.setRoles(roles);
        }
        return response;
    }

    public UserMongoResponseDto toResponseDto(UserMongo userMongo) {
        UserMongoResponseDto response = new UserMongoResponseDto();
        if (userMongo == null)
            return null;
        if (userMongo.getFirstName() != null)
            response.setFirstName(userMongo.getFirstName());
        if (userMongo.getLastName() != null)
            response.setLastName(userMongo.getLastName());
        if (userMongo.getEmail() != null)
            response.setEmail(userMongo.getEmail());
        if (userMongo.getUsername() != null)
            response.setUsername(userMongo.getUsername());
        if (userMongo.getRoles() != null) {
            val roles = userMongo.getRoles().stream().map(
                            roleMapper::mapStringToRole)
                    .collect(Collectors.toSet());
            response.setRoles(roles);
        }
        if (userMongo.getId() != null)
            response.setId(userMongo.getId());
        if (userMongo.getPhone() != null)
            response.setPhone(userMongo.getPhone());
        return response;

    }
}
