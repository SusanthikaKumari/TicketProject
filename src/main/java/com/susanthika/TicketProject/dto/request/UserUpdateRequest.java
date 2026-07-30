package com.susanthika.TicketProject.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateRequest {

    @Size(max = 150)
    private String firstName;

    @Size(max = 150)
    private String lastName;

    @Size(max = 200)
    private String email;

    @Size(min = 8, max = 255)
    private String password;

    private Long roleId;

    private Long departmentId;

}

