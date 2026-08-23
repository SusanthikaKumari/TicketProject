package com.susanthika.TicketProject.controller;

import com.susanthika.TicketProject.dto.request.LoginRequest;
import com.susanthika.TicketProject.dto.request.RegisterRequest;
import com.susanthika.TicketProject.dto.response.ApiResponse;
import com.susanthika.TicketProject.dto.response.AuthResponse;
import com.susanthika.TicketProject.service.AuthService;
import com.susanthika.TicketProject.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest){
        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponseUtil.success(
                                "Registration successful",
                                response,
                                HttpStatus.CREATED
                )
        );
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest){

        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Login successful",
                        response
                )
        );
    }

}
