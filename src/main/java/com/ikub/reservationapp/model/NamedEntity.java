package com.ikub.reservationapp.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class NamedEntity extends BaseEntity {

    @Column(name = "name")
    private String name;

}