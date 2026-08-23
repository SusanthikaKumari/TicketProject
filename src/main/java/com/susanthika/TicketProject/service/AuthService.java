package com.susanthika.TicketProject.service;

import com.susanthika.TicketProject.dto.request.LoginRequest;
import com.susanthika.TicketProject.dto.request.RegisterRequest;
import com.susanthika.TicketProject.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
}
