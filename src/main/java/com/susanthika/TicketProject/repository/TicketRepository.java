package com.susanthika.TicketProject.repository;

import com.susanthika.TicketProject.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsByTicketCode(String ticketCode);
    Optional<Ticket> findTicketByTicketCode(String ticketCode);
    List<Ticket> findByCustomerId(Long customerId);
    List<Ticket> findByAssignedAdminAgentId(Long adminId);
}




