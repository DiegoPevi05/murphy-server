package com.medicapp.server.doctors.repository;

import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.doctors.model.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    @Query("SELECT d FROM Doctor d WHERE d.user.firstname LIKE %:name%")
    Page<Doctor> findByNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.user.email = :email")
    Optional<Doctor> findByUserEmail(@Param("email") String email);

    @Query("SELECT d FROM Doctor d WHERE d.specialty = :specialty")
    Page<Doctor> findBySpecialtyContaining(@Param("specialty") Specialty specialty, Pageable pageable);
}
