package com.ikub.reservationapp.common.model;

import lombok.Data;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @LastModifiedBy
    private LocalDateTime lastModified;
    @CreatedDate
    private LocalDateTime createdOn;
    @CreatedBy
    private String createdBy;

    @PrePersist
    protected void prePersist() {
        if (this.createdOn == null)
            createdOn = LocalDateTime.now();
        if (this.lastModified == null)
            lastModified = LocalDateTime.now();
        if(this.createdBy == null)
            this.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreUpdate
    protected void preUpdate() {
        this.lastModified = LocalDateTime.now();
    }

    @PreRemove
    protected void preRemove() {
        this.lastModified = LocalDateTime.now();
    }
}