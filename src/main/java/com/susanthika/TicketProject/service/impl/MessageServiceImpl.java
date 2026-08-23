package com.susanthika.TicketProject.service.impl;

import com.susanthika.TicketProject.dto.request.MessageRequest;
import com.susanthika.TicketProject.dto.request.MessageUpdateRequest;
import com.susanthika.TicketProject.dto.response.MessageResponse;
import com.susanthika.TicketProject.entity.Message;
import com.susanthika.TicketProject.entity.Ticket;
import com.susanthika.TicketProject.entity.User;
import com.susanthika.TicketProject.exception.BadRequestException;
import com.susanthika.TicketProject.exception.ResourceNotFoundException;
import com.susanthika.TicketProject.repository.MessageRepository;
import com.susanthika.TicketProject.repository.TicketRepository;
import com.susanthika.TicketProject.repository.UserRepository;
import com.susanthika.TicketProject.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Override
    public MessageResponse sendCustomerMessage(MessageRequest messageRequest) {
        Ticket ticket = ticketRepository.findTicketByTicketCode(messageRequest.getTicketCode())
                .orElseThrow(()-> new ResourceNotFoundException("Ticket not found: "+ messageRequest.getTicketCode()));

        User customer = getCurrentUser();
/*
        if (!"CUSTOMER".equalsIgnoreCase(customer.getRole().getRoleName())){
            throw new BadRequestException("User is not a customer");
        }
 */
        if (!customer.getRole().isCustomer()) {
            throw new BadRequestException("User is not a customer");
        }

        if (!ticket.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException(
                    "You can only send messages to your own ticket");
        }

        Message message = modelMapper.map(messageRequest, Message.class);
        message.setTicket(ticket);
        message.setSender(customer);

        Message savedMessage = messageRepository.save(message);
        return mapToResponse(savedMessage);
    }

    @Override
    public MessageResponse sendAdminMessage(MessageRequest messageRequest) {
        Ticket ticket = ticketRepository.findTicketByTicketCode(messageRequest.getTicketCode())
                .orElseThrow(()-> new ResourceNotFoundException("Ticket not found: " + messageRequest.getTicketCode()));

        User admin = getCurrentUser();

        if (!admin.getRole().isAdmin()) {
            throw new BadRequestException("User is not an admin");
        }

        if (ticket.getAssignedAdminAgent()==null){
            throw new BadRequestException("No admin assigned to this ticket");
        }

        if (!ticket.getAssignedAdminAgent().getId().equals(admin.getId())){
            throw new BadRequestException("Only assigned admin can reply to this ticket");
        }

        Message message = modelMapper.map(messageRequest, Message.class);
        message.setTicket(ticket);
        message.setSender(admin);

        Message savedMessage = messageRepository.save(message);

        return mapToResponse(savedMessage);
    }

    @Override
    public List<MessageResponse> getMessagesByTicket(String ticketCode) {
        ticketRepository.findTicketByTicketCode(ticketCode)
                .orElseThrow(()-> new ResourceNotFoundException("Ticket not found: " + ticketCode));
        return messageRepository.findByTicketTicketCode(ticketCode)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MessageResponse updateCustomerMessage(Long messageId, MessageUpdateRequest messageUpdateRequest) {

        User customer = getCurrentUser();
        return updateMessage(messageId, messageUpdateRequest, customer);
    }

    @Override
    public MessageResponse updateAdminMessage(Long messageId, MessageUpdateRequest messageUpdateRequest) {

        User admin = getCurrentUser();
        return updateMessage(messageId, messageUpdateRequest, admin);
    }

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (User) authentication.getPrincipal();
    }

    private MessageResponse mapToResponse(Message message){
        MessageResponse response = modelMapper.map(message, MessageResponse.class);
        response.setTicketCode(message.getTicket().getTicketCode());
        response.setSenderName(message.getSender().getFirstName() + " " + message.getSender().getLastName());
        response.setSenderRole(message.getSender().getRole().getRoleName());

        return response;
    }


    private MessageResponse updateMessage(Long messageId, MessageUpdateRequest messageUpdateRequest, User currentUser){
        Message message = messageRepository.findById(messageId)
                .orElseThrow(()-> new ResourceNotFoundException("Message not found"));

        if (!message.getSender().getId().equals(currentUser.getId())){
            throw new BadRequestException("You can only edit your messages");
        }

        if (LocalDateTime.now().isAfter(message.getCreatedAt().plusMinutes(5))){
            throw new BadRequestException("Message can only edited within 5 minutes");
        }

        message.setMessage(messageUpdateRequest.getMessage());
        Message updatedMessage = messageRepository.save(message);

        return mapToResponse(updatedMessage);
    }

}
