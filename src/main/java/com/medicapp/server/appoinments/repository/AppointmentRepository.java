package com.medicapp.server.appoinments.repository;

import com.medicapp.server.appoinments.model.Appointment;
import com.medicapp.server.doctors.model.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    @Query("SELECT a FROM Appointment a WHERE YEAR(a.date) = YEAR(:date) AND MONTH(a.date) = MONTH(:date) AND DAY(a.date) = DAY(:date) AND a.user.id = :user_id")
    Page<Appointment> findByDateContaining(@Param("date") LocalDateTime date,@Param("user_id") Integer user_id, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE YEAR(a.date) = YEAR(:date) AND MONTH(a.date) = MONTH(:date) AND a.user.id = :user_id")
    Page<Appointment> findByDateContainingMonth(@Param("date") LocalDateTime date,@Param("user_id") Integer user_id, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.specialty = :specialty AND a.user.id = :user_id")
    Page<Appointment> findBySpecialtyContaining(@Param("specialty") Specialty specialty,@Param("user_id") Integer user_id, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.user.id = :user_id")
    Page<Appointment> findAllByUserId(@Param("user_id") Integer user_id, Pageable pageable);

}