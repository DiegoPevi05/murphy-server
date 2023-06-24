package com.medicapp.server.payments.utils;

import com.medicapp.server.payments.dto.InvoiceResponse;
import com.medicapp.server.payments.model.Invoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceHelper {

    public InvoiceResponse mapToInvoiceResponse(Invoice invoice){
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .price(invoice.getPrice())
                .details(invoice.getDetails())
                .appointmentDate(invoice.getAppointment().getDate())
                .userId(invoice.getAppointment().getUser().getId())
                .userName(invoice.getAppointment().getUser().getFirstname())
                .userSurname(invoice.getAppointment().getUser().getLastname())
                .userEmail(invoice.getAppointment().getUser().getEmail())
                .doctorId(invoice.getAppointment().getDoctor().getId())
                .doctorName(invoice.getAppointment().getDoctor().getUser().getFirstname())
                .doctorSurname(invoice.getAppointment().getDoctor().getUser().getLastname())
                .doctorSpecialization(invoice.getAppointment().getDoctor().getSpecialty().toString())
                .doctorEmail(invoice.getAppointment().getDoctor().getUser().getEmail())
                .build();
    }
}
