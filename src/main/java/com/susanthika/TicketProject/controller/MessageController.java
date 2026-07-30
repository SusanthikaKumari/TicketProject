package com.susanthika.TicketProject.controller;

import com.susanthika.TicketProject.dto.request.MessageRequest;
import com.susanthika.TicketProject.dto.request.MessageUpdateRequest;
import com.susanthika.TicketProject.dto.response.MessageResponse;
import com.susanthika.TicketProject.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/customer")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse sendCustomerMessage(@Valid @RequestBody MessageRequest messageRequest){
        return messageService.sendCustomerMessage(messageRequest);
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse sendAdminMessage(@Valid @RequestBody MessageRequest messageRequest){
        return messageService.sendAdminMessage(messageRequest);
    }

    @GetMapping("/ticket/{ticketCode}")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageResponse> getMessagesByTicket(@PathVariable String ticketCode){
        return messageService.getMessagesByTicket(ticketCode);
    }

    @PutMapping("/customer/{messageId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse updateMessageByCustomer(@PathVariable Long messageId, @Valid @RequestBody MessageUpdateRequest messageUpdateRequest){
        return messageService.updateCustomerMessage(messageId, messageUpdateRequest);
    }

    @PutMapping("/admin/{messageId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse updateMessageByAdmin(@PathVariable Long messageId, @Valid @RequestBody MessageUpdateRequest messageUpdateRequest){
        return messageService.updateAdminMessage(messageId, messageUpdateRequest);
    }
}
