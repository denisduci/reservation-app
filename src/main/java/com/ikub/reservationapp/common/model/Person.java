package com.ikub.reservationapp.common.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;

@Data
@MappedSuperclass
@RequiredArgsConstructor
public class Person extends BaseEntity {

    @Column(name = "first_name")
    @NotEmpty
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty
    private String lastName;

    public Person(String firstname) {
        this.firstName = firstname;
    }

}