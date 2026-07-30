package com.susanthika.TicketProject.dto.request;

import com.susanthika.TicketProject.entity.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title can not exceed 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

//    @NotBlank(message = "Priority is required")
    @NotNull(message = "Priority is required")
    private Priority priority;

//    @NotBlank(message = "Department is required")
    @NotNull(message = "Department is required")
    private Long departmentId;
}
