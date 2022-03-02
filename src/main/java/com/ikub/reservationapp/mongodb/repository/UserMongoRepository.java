package com.ikub.reservationapp.mongodb.repository;

import com.ikub.reservationapp.mongodb.model.NamesOnly;
import com.ikub.reservationapp.mongodb.model.UserMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMongoRepository extends MongoRepository<UserMongo, String> {

    boolean existsByUsername(String username);
    UserMongo findByUsername(String username);
    Optional<UserMongo> findByEmail(String email);

    @Query(value="{}", fields="{firstName : 1, lastName:1, _id : 0}")
    List<NamesOnly> findCustomValuesAndExcludeId();

    List<UserMongo> findByIdNotInAndRolesIn(List<String> ids, List<String> roles);
}
