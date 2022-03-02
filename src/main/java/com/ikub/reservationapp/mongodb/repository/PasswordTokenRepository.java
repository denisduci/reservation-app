package com.ikub.reservationapp.mongodb.repository;

import com.ikub.reservationapp.mongodb.model.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordTokenRepository extends MongoRepository<PasswordResetToken, String> {

    PasswordResetToken findByToken(String token);
}
