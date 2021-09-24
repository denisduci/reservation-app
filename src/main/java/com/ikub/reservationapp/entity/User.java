package com.ikub.reservationapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ikub.reservationapp.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "userE")
public class User extends BaseEntity {
    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private String phone;
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "USER_ROLES",
            joinColumns = {
                    @JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {
                    @JoinColumn(name = "ROLE_ID")})
    private Set<Role> roles;

}