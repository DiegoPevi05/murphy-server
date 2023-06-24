package com.medicapp.server.doctors.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="TimeSheet")
@Table(name="timeSheet")
public class TimeSheet {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(
            name = "doctor_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "doctor_id_fk"
            )
    )
    private Doctor doctor;

    @Column(
            name = "isAvailable",
            nullable = false
    )
    private Boolean isAvailable = true;
    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
}
