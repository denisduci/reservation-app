package com.ikub.reservationapp.users.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ikub.reservationapp.common.model.BaseEntity;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class UserEntity extends BaseEntity {

    //retrieve reserved appointments
    private String username;
    @JsonIgnore
    private String password;
    @Transient
    private String confirmPassword;
    private String email;
    private String phone;
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLES",
            joinColumns = {
                    @JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {
                    @JoinColumn(name = "ROLE_ID")})
    private Set<RoleEntity> roles;

}