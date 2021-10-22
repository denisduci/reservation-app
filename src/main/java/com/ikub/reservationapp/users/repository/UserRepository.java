package com.ikub.reservationapp.users.repository;

import com.ikub.reservationapp.users.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    @Query(value = "select u from UserEntity u left join fetch u.roles where u.username=:userName")
    Optional<UserEntity> findByUsername(@Param("userName") String userName);

    List<UserEntity> findAll();

    List<UserEntity> findByRolesName(String roleName);

    Optional<UserEntity> findByIdAndRolesName(Long id, String roleName);

    Page<UserEntity> findAll(Specification<UserEntity> spec, Pageable pageable);

    List<UserEntity> findAll(Specification<UserEntity> spec);

    Page<UserEntity> findAll(Pageable pageable);
}