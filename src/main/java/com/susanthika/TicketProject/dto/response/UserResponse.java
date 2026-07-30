package com.susanthika.TicketProject.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String department;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
