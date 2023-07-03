package com.medicapp.server.messages.repository;

import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.messages.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("SELECT m FROM Message m WHERE (m.sender_id = :senderId AND m.receiver_id = :receiverId) OR (m.sender_id = :receiverId AND m.receiver_id = :senderId) ORDER BY m.id ASC ")
    Page<Message> findBySenderIdAndReceiverId(@Param("senderId") String senderId,@Param("receiverId") String receiverId, Pageable pageable);


}
