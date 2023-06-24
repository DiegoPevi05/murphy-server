package com.medicapp.server.payments.controller;

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
@RequestMapping("/api/v1/admin/invoice")
@RequiredArgsConstructor
public class InvoiceAdminController {

    private final InvoiceService invoiceService;
    @GetMapping
    public Page<InvoiceResponse> getInvoices(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "userId", defaultValue = "") int userId
    ){
        return invoiceService.getInvoicesUser(page,size,userId);
    }

    @PostMapping
    public ResponseEntity<String> registerInvoice(@RequestBody InvoiceRequest invoiceRequest) throws MessagingException {
        invoiceService.addInvoice(invoiceRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Invoice created successfully.");
    }

    @DeleteMapping(path="{Invoice_id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable("Invoice_id") Integer Invoice_id){
        invoiceService.deleteInvoice(Invoice_id);
        return ResponseEntity.status(HttpStatus.OK).body("Invoice deleted Successfully.");
    }
}
