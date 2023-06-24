package com.medicapp.server.appoinments.model;

import com.medicapp.server.authentication.model.User;
import com.medicapp.server.doctors.model.Doctor;
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
@Entity(name="Appointment")
@Table(name="appointment")
public class Appointment {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "user_id_fk"
            )
    )
    private User user;

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

    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date;
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
}
