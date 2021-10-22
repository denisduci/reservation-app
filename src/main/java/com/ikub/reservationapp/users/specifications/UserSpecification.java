package com.ikub.reservationapp.users.specifications;

import com.ikub.reservationapp.appointments.utils.DateUtil;
import com.ikub.reservationapp.users.dto.UserSearchRequestDto;
import com.ikub.reservationapp.users.entity.RoleEntity;
import com.ikub.reservationapp.users.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserSpecification {

    public Specification<UserEntity> getUsers(UserSearchRequestDto request) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<String> roleNamePatient = new ArrayList<>();
            roleNamePatient.add("PATIENT");
            final SetJoin<UserEntity, RoleEntity> userRoleJoin = root.joinSet("roles");
            Predicate isPatient = userRoleJoin.get("name").in(roleNamePatient);

            if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                Predicate predicateForEmail = criteriaBuilder.equal(root.get("email"), request.getEmail());
                predicates.add(predicateForEmail);
            }
            if (request.getName() != null && !request.getName().isEmpty()) {
                Predicate firstNameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + request.getName() + "%");
                Predicate lastNameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + request.getName() + "%");
                Predicate firstNameLikeOrLastNameLike = criteriaBuilder.or(firstNameLike, lastNameLike);
                Predicate firstNameLikeOrLastNameLikeAndPatient = criteriaBuilder.and(firstNameLikeOrLastNameLike, isPatient);
                predicates.add(firstNameLikeOrLastNameLikeAndPatient);
            }
            query.orderBy(criteriaBuilder.desc(root.get("firstName")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}