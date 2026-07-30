package com.susanthika.TicketProject.service;

import com.susanthika.TicketProject.dto.request.DepartmentRequest;
import com.susanthika.TicketProject.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse findDepartmentById(Long id);
    List<DepartmentResponse> findAllDepartments();
    DepartmentResponse createDepartment(DepartmentRequest departmentRequest);
    DepartmentResponse updateDepartment(Long id, DepartmentRequest departmentRequest);
    void deleteDepartmentById(Long id);
}
