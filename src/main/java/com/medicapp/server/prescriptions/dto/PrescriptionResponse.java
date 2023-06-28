package com.medicapp.server.prescriptions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionResponse {

    private Integer prescriptionId;
    private Integer appointmentId;
    private String prescription_content;
    private String prescription_image_url;
}
