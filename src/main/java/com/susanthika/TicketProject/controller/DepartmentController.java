package com.susanthika.TicketProject.controller;

import com.susanthika.TicketProject.dto.request.DepartmentRequest;
import com.susanthika.TicketProject.dto.response.DepartmentResponse;
import com.susanthika.TicketProject.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DepartmentResponse findById(@PathVariable Long id){
        return departmentService.findDepartmentById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DepartmentResponse> findAllDepartments(){
        return departmentService.findAllDepartments();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse createDepartment(@Valid @RequestBody DepartmentRequest departmentRequest){
        return departmentService.createDepartment(departmentRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentRequest departmentRequest){
        return departmentService.updateDepartment(id, departmentRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDepartment(@PathVariable Long id){
        departmentService.deleteDepartmentById(id);
    }
}
