package com.susanthika.TicketProject.dto.response;

import com.susanthika.TicketProject.entity.enums.Priority;
import com.susanthika.TicketProject.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketResponse {

    private String ticketCode;
    private String title;
    private String description;
    private Priority priority;
    private Status status;
    private String customerName;
    private String assignedAdminAgentName;
    private String departmentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
