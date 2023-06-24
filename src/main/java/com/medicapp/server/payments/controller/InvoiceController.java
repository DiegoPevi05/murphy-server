package com.medicapp.server.payments.controller;

import com.medicapp.server.appoinments.dto.AppointmentRequest;
import com.medicapp.server.doctors.dto.DoctorResponse;
import com.medicapp.server.payments.dto.InvoiceRequest;
import com.medicapp.server.payments.dto.InvoiceResponse;
import com.medicapp.server.payments.service.InvoiceService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    @GetMapping
    public Page<InvoiceResponse> getInvoices(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        return invoiceService.getInvoicesUserByUser(page,size);
    }

    @PostMapping
    public ResponseEntity<String> addInvoice(@RequestBody InvoiceRequest invoiceRequest) throws MessagingException {
        invoiceService.addInvoiceUser(invoiceRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Invoice created successfully.");
    }
}
