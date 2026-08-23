package com.susanthika.TicketProject.controller;

import com.susanthika.TicketProject.dto.request.MessageRequest;
import com.susanthika.TicketProject.dto.request.MessageUpdateRequest;
import com.susanthika.TicketProject.dto.response.ApiResponse;
import com.susanthika.TicketProject.dto.response.MessageResponse;
import com.susanthika.TicketProject.service.MessageService;
import com.susanthika.TicketProject.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/customer")
    public ResponseEntity<ApiResponse<MessageResponse>> sendCustomerMessage(@Valid @RequestBody MessageRequest messageRequest){
       MessageResponse response = messageService.sendCustomerMessage(messageRequest);
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(
                       ApiResponseUtil.success(
                               "Message created successfully",
                               response,
                               HttpStatus.CREATED
                       )
               );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<MessageResponse>> sendAdminMessage(@Valid @RequestBody MessageRequest messageRequest){
        MessageResponse response = messageService.sendAdminMessage(messageRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponseUtil.success(
                                "Message created successfully",
                                response,
                                HttpStatus.CREATED
                        )
                );
    }

    @PreAuthorize("hasAnyRole('ADMIN' , 'MANAGER')")
    @GetMapping("/ticket/{ticketCode}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessagesByTicket(@PathVariable String ticketCode){
        List<MessageResponse> response = messageService.getMessagesByTicket(ticketCode);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Message retrieved successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/customer/{messageId}")
    public ResponseEntity<ApiResponse<MessageResponse>> updateMessageByCustomer(@PathVariable Long messageId, @Valid @RequestBody MessageUpdateRequest messageUpdateRequest){
        MessageResponse response = messageService.updateCustomerMessage(messageId, messageUpdateRequest);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Message updated successfully",
                        response
                )
        );

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{messageId}")
    public ResponseEntity<ApiResponse<MessageResponse>> updateMessageByAdmin(@PathVariable Long messageId, @Valid @RequestBody MessageUpdateRequest messageUpdateRequest){
        MessageResponse response = messageService.updateAdminMessage(messageId, messageUpdateRequest);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Message updated successfully",
                        response
                )
        );
    }
}
