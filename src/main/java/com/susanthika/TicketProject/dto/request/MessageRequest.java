package com.susanthika.TicketProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageRequest {

    @NotBlank(message = "Ticket code is required")
    private String ticketCode;

    @NotBlank(message = "Message is required")
    private String message;
}
