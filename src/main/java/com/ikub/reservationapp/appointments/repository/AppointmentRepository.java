package com.ikub.reservationapp.appointments.repository;

import com.ikub.reservationapp.appointments.dto.reports.ReportDBResponseDto;
import com.ikub.reservationapp.appointments.entity.AppointmentEntity;
import com.ikub.reservationapp.common.enums.Status;
import com.ikub.reservationapp.users.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends CrudRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findByStatus(Status status);

    //@Query("SELECT a FROM AppointmentEntity a WHERE a.appointmentDate =:appointmentDateTime")
    @Query("SELECT a FROM AppointmentEntity a WHERE a.appointmentDate =:appointmentDateTime and a.status<>2")
    List<AppointmentEntity> findByAppointmentDate(@Param("appointmentDateTime") LocalDate appointmentDate);

    @Query("SELECT a FROM AppointmentEntity a WHERE (:appointmentStartTime >= a.startTime AND :appointmentEndTime <= a.endTime) AND a.doctor=:doctorId AND a.status NOT IN (2,5,6,7)")
    // working version @Query("SELECT a FROM AppointmentEntity a WHERE ((:appointmentStartTime >= a.startTime AND :appointmentEndTime <= a.endTime) OR (:appointmentStartTime BETWEEN a.startTime AND a.endTime)) AND a.doctor=:doctorId")
    List<AppointmentEntity> findByDoctorAvailability(@Param("doctorId") UserEntity doctorId,
                                                     @Param("appointmentStartTime") LocalDateTime appointmentStartTime,
                                                     @Param("appointmentEndTime") LocalDateTime appointmentEndTime);

    @Query("SELECT a FROM AppointmentEntity a WHERE (:appointmentStartTime >= a.startTime AND :appointmentEndTime <= a.endTime) AND a.patient=:patientId AND a.status NOT IN (2,5,6,7)")
    List<AppointmentEntity> findAppointmentForPatient(@Param("patientId") UserEntity patientId,
                                                      @Param("appointmentStartTime") LocalDateTime appointmentStartTime,
                                                      @Param("appointmentEndTime") LocalDateTime appointmentEndTime);

    List<AppointmentEntity> findByStatusAndPatient(Status status, UserEntity patient);
    List<AppointmentEntity> findByPatient(UserEntity patient);
    List<AppointmentEntity> findByDoctor(UserEntity doctor);
    List<AppointmentEntity> findAll();
    List<AppointmentEntity> findByStatusAndDoctor(Status status, UserEntity doctor);

    @Query(value="SELECT new com.ikub.reservationapp.appointments.dto.reports.ReportDBResponseDto(date_trunc('week', a.appointmentDate), count(*), a.status) FROM " +
            "AppointmentEntity a GROUP BY date_trunc('week', a.appointmentDate), a.status ORDER BY date_trunc('week',a.appointmentDate) DESC")
    List<ReportDBResponseDto> findWeeklyReports();

    @Query(value="SELECT new com.ikub.reservationapp.appointments.dto.reports.ReportDBResponseDto(date_trunc('month', a.appointmentDate), count(*), a.status) FROM " +
            "AppointmentEntity a GROUP BY date_trunc('month', a.appointmentDate), a.status ORDER BY date_trunc('month',a.appointmentDate) DESC")
    List<ReportDBResponseDto> findMonthlyReports();

    @Query(value = "SELECT week AS date, name AS firstname, surname AS lastname, max(COUNTERV) AS numberOfAppointments, status AS status  FROM(\n" +
            "\t\tselect date_trunc('MONTH',appointment_date) AS week,\n" +
            "\t\td.first_name AS name, d.last_name AS surname, count(a.doctor_id) AS COUNTERV, a.status AS status FROM users d JOIN\n" +
            "\t\tappointment a ON a.doctor_id=d.id WHERE a.status=4\n" +
            "GROUP BY week,\n" +
            "d.first_name, d.last_name,status  \n" +
            ") AS result GROUP BY \n" +
            "result.week,\n" +
            "result.name,\n" +
            "result.surname,\n" +
            "result.status\n" +
            "ORDER BY numberOfAppointments DESC,\n" +
            " week DESC", nativeQuery = true)
    List<Object[]> findDoctorsReports();
}
