package com.susanthika.TicketProject.dto.request;

import com.susanthika.TicketProject.entity.enums.Priority;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketCustomerUpdateRequest {

    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;
    private String description;
    private Priority priority;
    private Long departmentId;
}
