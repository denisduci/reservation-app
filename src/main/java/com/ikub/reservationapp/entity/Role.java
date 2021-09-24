package com.ikub.reservationapp.entity;

import com.ikub.reservationapp.model.BaseEntity;
import lombok.Data;
import javax.persistence.*;

@Entity
@Data
public class Role extends BaseEntity {
    private String name;
    private String description;
}