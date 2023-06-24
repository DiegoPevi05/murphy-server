package com.medicapp.server.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequest {
    private Integer appointmentId;
    private String details;
    private Float price;

    private String paymentCode_1;
    private String paymentCode_2;

}
