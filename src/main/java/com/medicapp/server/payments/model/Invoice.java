package com.medicapp.server.payments.model;

import com.medicapp.server.appoinments.model.Appointment;
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
@Entity(name="Invoice")
@Table(name="invoice")
public class Invoice {
        @Id
        @GeneratedValue
        private Integer id;

        private Float price;

        private String details;

        private Boolean isPaid = false;

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

        private String paymentCode_1;
        private String paymentCode_2;

        @CreationTimestamp
        @Column(name = "created_date", nullable = false, updatable = false)
        private LocalDateTime createdDate;

        @UpdateTimestamp
        @Column(name = "updated_date", nullable = false)
        private LocalDateTime updatedDate;
}
