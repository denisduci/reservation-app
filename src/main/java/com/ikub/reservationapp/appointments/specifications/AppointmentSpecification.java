package com.ikub.reservationapp.appointments.specifications;

import com.ikub.reservationapp.appointments.dto.AppointmentSearchRequestDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.appointments.utils.DateUtil;
import com.ikub.reservationapp.users.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class AppointmentSpecification {

    public Specification<AppointmentEntity> getAppointments(AppointmentSearchRequestDto request) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getPatientName() != null) {
                Join<AppointmentEntity, UserEntity> appointmentUserJoin = root.join("patient");
                Predicate firstNameLike = criteriaBuilder.like(criteriaBuilder.lower(appointmentUserJoin.get("firstName")), "%" + request.getPatientName() + "%");
                Predicate lastNameLike = criteriaBuilder.like(criteriaBuilder.lower(appointmentUserJoin.get("lastName")), "%" + request.getPatientName() + "%");
                Predicate firstNameLikeOrLastNameLike = criteriaBuilder.or(firstNameLike, lastNameLike);
                predicates.add(firstNameLikeOrLastNameLike);
            }

            if (request.getDoctorName() != null) {
                Join<AppointmentEntity, UserEntity> appointmentDoctorJoin = root.join("doctor");
                Predicate firstNameLike = criteriaBuilder.like(criteriaBuilder.lower(appointmentDoctorJoin.get("firstName")),
                        "%" + request.getDoctorName() + "%");
                Predicate lastNameLike = criteriaBuilder.like(criteriaBuilder.lower(appointmentDoctorJoin.get("lastName")),
                        "%" + request.getDoctorName() + "%");
                Predicate firstNameOrLastNameLike = criteriaBuilder.or(firstNameLike, lastNameLike);
                predicates.add(firstNameOrLastNameLike);
            }

            if (request.getDate() != null) {
                LocalDate localDateFormat = DateUtil.validateDateFormat(request.getDate());
                Predicate dateEqual = criteriaBuilder.equal(root.get("appointmentDate"), localDateFormat);
                predicates.add(dateEqual);
            }

            query.orderBy(criteriaBuilder.desc(root.get("appointmentDate")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}