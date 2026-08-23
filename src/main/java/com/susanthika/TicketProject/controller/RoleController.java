package com.susanthika.TicketProject.controller;

import com.susanthika.TicketProject.dto.request.RoleRequest;
import com.susanthika.TicketProject.dto.response.ApiResponse;
import com.susanthika.TicketProject.dto.response.RoleResponse;
import com.susanthika.TicketProject.service.RoleService;
import com.susanthika.TicketProject.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> findRoleById(@PathVariable("id") Long id){
        RoleResponse response = roleService.findRoleById(id);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Role retrieved successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> findAllRoles(){
        List<RoleResponse> response = roleService.findAllRoles();

        String message = response.isEmpty() ? "No roles found" : "Roles retrieved successfully";

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        message,
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleRequest roleRequest){
         RoleResponse response = roleService.createRole(roleRequest);
         return ResponseEntity.status(HttpStatus.CREATED)
                 .body(
                         ApiResponseUtil.success(
                                 "Role created successfully",
                                 response,
                                 HttpStatus.CREATED
                         )
                 );
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@PathVariable("id") Long id, @Valid @RequestBody RoleRequest roleRequest){
        RoleResponse response = roleService.updateRole(id, roleRequest);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Role updated successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable("id") Long id){
        roleService.deleteRoleById(id);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Role deleted successfully",
                        null
                )
        );
    }
}
