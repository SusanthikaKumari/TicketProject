package com.susanthika.TicketProject.service;

import com.susanthika.TicketProject.dto.request.MessageRequest;
import com.susanthika.TicketProject.dto.request.MessageUpdateRequest;
import com.susanthika.TicketProject.dto.response.MessageResponse;

import java.util.List;

public interface MessageService {

    MessageResponse sendCustomerMessage(MessageRequest messageRequest);
    MessageResponse sendAdminMessage(MessageRequest messageRequest);
    List<MessageResponse> getMessagesByTicket(String ticketCode);
    MessageResponse updateCustomerMessage(Long messageId, MessageUpdateRequest messageUpdateRequest);
    MessageResponse updateAdminMessage(Long messageId, MessageUpdateRequest messageUpdateRequest);
}
