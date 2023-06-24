package com.medicapp.server.payments.service;

import com.medicapp.server.appoinments.model.Appointment;
import com.medicapp.server.appoinments.repository.AppointmentRepository;
import com.medicapp.server.authentication.model.User;
import com.medicapp.server.authentication.service.AuthenticationService;
import com.medicapp.server.config.ExceptionHandlerConfig;
import com.medicapp.server.email.EmailService;
import com.medicapp.server.payments.dto.InvoiceRequest;
import com.medicapp.server.payments.dto.InvoiceResponse;
import com.medicapp.server.payments.model.Invoice;
import com.medicapp.server.payments.repository.InvoiceRepository;
import com.medicapp.server.payments.utils.InvoiceHelper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final AuthenticationService authenticationService;
    private final AppointmentRepository appointmentRepository;

    private final EmailService emailService;
    private final InvoiceHelper invoiceHelper;

    public Page<InvoiceResponse>getInvoicesUserByUser(int page, int size){
        User user = authenticationService.getUserAuthorize();
        return getInvoicesUser(page, size, user.getId());
    }
    public Page<InvoiceResponse> getInvoicesUser(int page, int size, Integer UserId){
        Pageable pageable = PageRequest.of(page, size);

        Page<Invoice> invoicePage = invoiceRepository.findByUserId(UserId,pageable);

        List<InvoiceResponse> invoiceResponses = invoicePage.getContent().stream()
                .map(invoiceHelper::mapToInvoiceResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(invoiceResponses, pageable, invoicePage.getTotalElements());
    }

    public void addInvoiceUser(InvoiceRequest invoiceRequest) throws MessagingException {
        User user = authenticationService.getUserAuthorize();
        Appointment appointment = appointmentRepository.findById(invoiceRequest.getAppointmentId())
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException("Appointment not found"));
        if(appointment.getUser().getId() != user.getId()){
            throw new ExceptionHandlerConfig.ResourceNotFoundException("You dont have permission to add invoice to this appointment");
        }
        addInvoice(invoiceRequest);
    }
    public void addInvoice(InvoiceRequest invoiceRequest) throws MessagingException {
        Appointment appointment = appointmentRepository.findById(invoiceRequest.getAppointmentId())
                .orElseThrow(() -> new ExceptionHandlerConfig.ResourceNotFoundException("Appointment not found"));

        if(!isValidPayment(invoiceRequest.getPaymentCode_1(), invoiceRequest.getPaymentCode_2())){
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Payment codes are not valid");
        }

        emailService.send(appointment.getUser().getEmail(),
                "Your Purchase was successfull",
                "Thanks for Buy our Service",
                appointment.getUser().getFirstname(),
                "You have buy our service with Doctor "+appointment.getDoctor().getUser().getFirstname()+" "+appointment.getDoctor().getUser().getLastname()+"\n" +
                        "The specialty you have choosen is" + appointment.getDoctor().getSpecialty().toString()+"\n" +
                        "it cost you "+invoiceRequest.getPrice()+"\n" +
                        "Thanks for buy our service",
                null,
                "message");

        Invoice invoice = Invoice.builder()
                .price(invoiceRequest.getPrice())
                .details(invoiceRequest.getDetails())
                .appointment(appointment)
                .isPaid(true)
                .paymentCode_1(invoiceRequest.getPaymentCode_1())
                .paymentCode_2(invoiceRequest.getPaymentCode_2())
                .build();
        invoiceRepository.save(invoice);


    }

    public void deleteInvoice(int Invoice_id){
        boolean exists = invoiceRepository.existsById(Invoice_id);
        if(!exists){
            throw new ExceptionHandlerConfig.ResourceNotFoundException("Invoice with id "+Invoice_id+" does not exist");
        }
        invoiceRepository.deleteById(Invoice_id);
    }

    private boolean isValidPayment(String PaymentCode_1, String PaymentCode_2){
        return true;
    }

}
