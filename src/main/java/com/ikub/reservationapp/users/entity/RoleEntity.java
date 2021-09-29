package com.ikub.reservationapp.users.entity;

import com.ikub.reservationapp.common.model.BaseEntity;
import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "role")
public class RoleEntity extends BaseEntity {
    private String name;
    private String description;
}