package com.medicapp.server.prescriptions.repository;


import com.medicapp.server.prescriptions.model.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {

    @Query("SELECT p FROM Prescription p WHERE p.appointment.user.id = :user_id")
    Page<Prescription> findByUserContaining(@Param("user_id") Integer user_id, Pageable pageable);

    @Query("SELECT p FROM Prescription p WHERE p.appointment.id = :appointment_id")
    Page<Prescription> findByAppointmentContaining(@Param("appointment_id") Integer appointment_id, Pageable pageable);

}
