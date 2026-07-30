package com.susanthika.TicketProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageUpdateRequest {

    @NotBlank(message = "Message is requires")
    private String message;
}
