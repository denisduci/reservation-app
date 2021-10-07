package com.ikub.reservationapp.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ikub.reservationapp.common.model.BaseEntity;
import com.ikub.reservationapp.common.model.Person;
import lombok.Data;
import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class UserEntity extends Person {

    private String username;
    @JsonIgnore
    private String password;
    @Transient
    private String confirmPassword;
    private String email;
    private String phone;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLES",
            joinColumns = {
                    @JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {
                    @JoinColumn(name = "ROLE_ID")})
    private Set<RoleEntity> roles;

}