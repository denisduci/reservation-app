package com.ikub.reservationapp.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("PasswordReset")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("PasswordReset")
public class PasswordResetToken {
    @Id
    private String id;
    private String token;
    private String user;
    private LocalDate expiryDate;
}