package com.susanthika.TicketProject.dto.request;

import com.susanthika.TicketProject.entity.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketAdminUpdateRequest {

    @NotNull(message = "Status is required")
    private Status status;
}
