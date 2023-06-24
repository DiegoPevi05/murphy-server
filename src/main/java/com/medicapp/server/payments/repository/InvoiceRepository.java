package com.medicapp.server.payments.repository;


;
import com.medicapp.server.payments.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    @Query("SELECT i FROM Invoice i WHERE i.appointment.user.id = :userId")
    Page<Invoice> findByUserId(@Param("userId") Integer userId, Pageable pageable);
}
