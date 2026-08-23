package com.susanthika.TicketProject.dto.request;

import com.susanthika.TicketProject.annotations.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 150)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 150)
    private String lastName;

    @NotBlank(message = "Email is required")
    //@Email(message = "Invalid email address")
    @ValidEmail(message = "Invalid email address")
    @Size(max = 200)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255")
    private String password;
}
