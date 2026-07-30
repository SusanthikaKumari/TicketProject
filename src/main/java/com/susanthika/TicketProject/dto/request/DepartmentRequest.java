package com.susanthika.TicketProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DepartmentRequest {

    @NotBlank(message = "Department name is required")
    @Size(max = 100)
    private String departmentName;
}
