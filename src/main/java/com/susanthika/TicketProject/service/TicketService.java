package com.susanthika.TicketProject.service;

import com.susanthika.TicketProject.dto.request.TicketAdminUpdateRequest;
import com.susanthika.TicketProject.dto.request.TicketAssignRequest;
import com.susanthika.TicketProject.dto.request.TicketCustomerUpdateRequest;
import com.susanthika.TicketProject.dto.request.TicketRequest;
import com.susanthika.TicketProject.dto.response.TicketResponse;

import java.util.List;

public interface TicketService {

    TicketResponse createTicket(TicketRequest ticketRequest);
    TicketResponse findTicketByTicketCode(String ticketCode);
    TicketResponse assignTicketToAdmin(String ticketCode, TicketAssignRequest ticketAssignRequest);
    TicketResponse updateTicketStatusByAdmin(String ticketCode, TicketAdminUpdateRequest ticketAdminUpdateRequest);
    TicketResponse updateTicketByCustomer(String ticketCode, TicketCustomerUpdateRequest ticketCustomerUpdateRequest);
    void deleteTicketByTicketCode(String ticketCode);
    List<TicketResponse> findCustomerTickets();
    List<TicketResponse> findAllAdminTickets();
    List<TicketResponse> findTicketsByAdminId(Long adminId);
    List<TicketResponse> findAllTicketsByManager();
}
