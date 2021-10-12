package com.ikub.reservationapp.common.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreatedDate
    private LocalDate createdOn;
    @LastModifiedDate
    private LocalDate lastModifiedOn;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String lastModifiedBy;

    @PrePersist
    protected void prePersist() {
        if (this.createdOn == null)
            createdOn = LocalDate.now();
        if (this.lastModifiedOn == null)
            lastModifiedOn = LocalDate.now();
        if (this.createdBy == null)
            this.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        if (this.lastModifiedBy == null)
            this.setLastModifiedBy(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreUpdate
    protected void preUpdate() {
            this.setLastModifiedOn(LocalDate.now());
            this.setLastModifiedBy(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreRemove
    protected void preRemove() {
            this.setLastModifiedOn(LocalDate.now());
            this.setLastModifiedBy(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}