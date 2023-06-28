package com.medicapp.server.prescriptions.model;

import com.medicapp.server.appoinments.model.Appointment;
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
@Entity(name="Prescription")
@Table(name="prescription")
public class Prescription {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(
            name = "appointment_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "appointment_id_fk"
            )
    )
    private Appointment appointment;

    private String prescription_content;

    @Column(
            name="profile_key",
            nullable = true,
            columnDefinition = "TEXT"
    )
    private String prescription_image_key;

    @Column(
            name="profile_url",
            nullable = true,
            columnDefinition = "TEXT"
    )
    private String prescription_image_url;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
}
