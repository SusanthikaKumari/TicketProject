package com.susanthika.TicketProject.repository;

import com.susanthika.TicketProject.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTicketTicketCode(String ticketCode);
}
