package com.medicapp.server.doctors.model;

import com.medicapp.server.authentication.model.User;
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
@Entity(name="Doctor")
@Table(name="doctor")
public class Doctor {
    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Specialty specialty;
    @Column(
            name = "description",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String description;
    @Column(
            name = "details",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String details;
    @Column(
            name="rating",
            nullable = false,
            columnDefinition = "DECIMAL(10,2)"
    )
    private Float rating;
    @Column(
            name="cost",
            nullable = false,
            columnDefinition = "DECIMAL(10,2)"
    )
    private Float cost;

    @OneToOne
    @JoinColumn(
            name="user_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name="user_item_fk"
            )
    )
    private User user;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
}
