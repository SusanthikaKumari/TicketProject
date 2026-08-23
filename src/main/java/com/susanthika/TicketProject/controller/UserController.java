package com.susanthika.TicketProject.controller;

import com.susanthika.TicketProject.dto.request.UserRequest;
import com.susanthika.TicketProject.dto.request.UserUpdateRequest;
import com.susanthika.TicketProject.dto.response.ApiResponse;
import com.susanthika.TicketProject.dto.response.UserResponse;
import com.susanthika.TicketProject.service.UserService;
import com.susanthika.TicketProject.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") Long id){
        UserResponse response = userService.findUserById(id);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "User retrieved successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(){
        List<UserResponse> response = userService.findAllUsers();

        String message = response.isEmpty() ? "No User found" : "Users retrieved successfully";

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                       message,
                       response
                )
        );
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest userRequest){
        UserResponse response = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponseUtil.success(
                                "User created successfully",
                                response,
                                HttpStatus.CREATED
                        )
                );
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest){
        UserResponse response = userService.updateUser(id, userUpdateRequest);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "User updated successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") Long id){
        userService.deleteUserById(id);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "User deleted successfully",
                        null
                )
        );
    }

}
