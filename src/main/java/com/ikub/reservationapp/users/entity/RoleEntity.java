package com.ikub.reservationapp.users.entity;

import com.ikub.reservationapp.common.model.BaseEntity;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Data
@Table(name = "role")
@EntityListeners(AuditingEntityListener.class)
public class RoleEntity extends BaseEntity {
    private String name;
    private String description;
}