package com.susanthika.TicketProject.service;

import com.susanthika.TicketProject.dto.request.UserRequest;
import com.susanthika.TicketProject.dto.request.UserUpdateRequest;
import com.susanthika.TicketProject.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse findUserById(Long id);
    List<UserResponse> findAllUsers();
    UserResponse createUser(UserRequest userRequest);
    UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest);
    void deleteUserById(Long id);
}
