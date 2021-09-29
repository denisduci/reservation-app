package com.ikub.reservationapp.users.repository;

import com.ikub.reservationapp.users.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    @Query(value = "select u from UserEntity u left join fetch u.roles where u.username=:userName")
    UserEntity findByUsername(@Param("userName") String userName);

    List<UserEntity> findAll();
}