package com.medicapp.server.prescriptions.utils;

import com.medicapp.server.prescriptions.dto.PrescriptionResponse;
import com.medicapp.server.prescriptions.model.Prescription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrescriptionHelper {

    public PrescriptionResponse mapToPrescriptionResponse(Prescription prescription){
        return PrescriptionResponse.builder()
                .prescriptionId(prescription.getId())
                .appointmentId(prescription.getAppointment().getId())
                .prescription_content(prescription.getPrescription_content())
                .prescription_image_url(prescription.getPrescription_image_url())
                .build();
    }
}
