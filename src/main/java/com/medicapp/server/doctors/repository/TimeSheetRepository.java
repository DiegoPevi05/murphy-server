package com.medicapp.server.doctors.repository;


import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.doctors.model.TimeSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimeSheetRepository extends JpaRepository<TimeSheet, Integer> {
    @Query(value="SELECT t FROM TimeSheet t WHERE EXTRACT(YEAR FROM t.date) = EXTRACT(YEAR FROM CAST(:date AS timestamp)) AND EXTRACT(MONTH FROM t.date) = EXTRACT(MONTH FROM CAST(:date AS timestamp)) AND EXTRACT(DAY FROM t.date) = EXTRACT(DAY FROM CAST(:date AS timestamp)) AND t.doctor.id = :doctorId AND t.isAvailable = true")
    List<TimeSheet> findByDateContaining(@Param("date") LocalDateTime date, @Param("doctorId") Integer doctorId);

    @Query(value = "SELECT t FROM TimeSheet t WHERE EXTRACT(YEAR FROM t.date) = EXTRACT(YEAR FROM CAST(:date AS timestamp)) AND EXTRACT(WEEK FROM t.date) = EXTRACT(WEEK FROM CAST(:date AS timestamp)) AND t.doctor.id = :doctorId AND t.isAvailable = true")
    List<TimeSheet> findByDateContainingWeek(@Param("date") LocalDateTime date, @Param("doctorId") Integer doctorId);

    @Query(value="SELECT t FROM TimeSheet t WHERE t.doctor = :doctor")
    List<TimeSheet> findByDoctor(@Param("doctor") Doctor doctor);

    @Query(value="SELECT t FROM TimeSheet t WHERE t.date = CAST(:date AS timestamp) AND t.doctor = :doctor")
    Optional<TimeSheet> findTimeSheetByDate(@Param("date") LocalDateTime date, @Param("doctor") Doctor doctor);


}
