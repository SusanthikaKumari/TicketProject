package com.susanthika.TicketProject.controller;

import com.susanthika.TicketProject.dto.request.DepartmentRequest;
import com.susanthika.TicketProject.dto.response.ApiResponse;
import com.susanthika.TicketProject.dto.response.DepartmentResponse;
import com.susanthika.TicketProject.service.DepartmentService;
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
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> findById(@PathVariable("id") Long id){
        DepartmentResponse response = departmentService.findDepartmentById(id);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Department retrieved successfully",
                        response
                )
        );
    }


    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> findAllDepartments(){
        List<DepartmentResponse> response = departmentService.findAllDepartments();

        String message = response.isEmpty() ? "No department found" : "Departments retrieved successfully";

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        message,
                        response
                        // HttpStatus.OK.value()
                )
        );

    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody DepartmentRequest departmentRequest){
        DepartmentResponse response = departmentService.createDepartment(departmentRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponseUtil.success(
                                "Department created successfully",
                                response,
                                HttpStatus.CREATED
                        )
                );
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(@PathVariable("id") Long id, @Valid @RequestBody DepartmentRequest departmentRequest){
        DepartmentResponse response = departmentService.updateDepartment(id, departmentRequest);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Department updated successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable("id") Long id){
        departmentService.deleteDepartmentById(id);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Department deleted successfully",
                        null
                )
        );
    }
}
