package com.susanthika.TicketProject.service.impl;

import com.susanthika.TicketProject.dto.request.DepartmentRequest;
import com.susanthika.TicketProject.dto.response.DepartmentResponse;
import com.susanthika.TicketProject.entity.Department;
import com.susanthika.TicketProject.exception.ResourceNotFoundException;
import com.susanthika.TicketProject.repository.DepartmentRepository;
import com.susanthika.TicketProject.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public DepartmentResponse findDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Department not fount; "+ id));
        return modelMapper.map(department, DepartmentResponse.class);
    }

    @Override
    public List<DepartmentResponse> findAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(department -> modelMapper.map(department, DepartmentResponse.class))
                .toList();
    }

    @Override
    public DepartmentResponse createDepartment(DepartmentRequest departmentRequest) {
        Department department = modelMapper.map(departmentRequest, Department.class);
        Department createdDepartment = departmentRepository.save(department);
        return modelMapper.map(createdDepartment, DepartmentResponse.class);
    }

    @Override
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest departmentRequest) {
        Department existingDepartment = departmentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Department not found"));
        modelMapper.map(departmentRequest, existingDepartment);
        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return modelMapper.map(updatedDepartment, DepartmentResponse.class);
    }

    @Override
    public void deleteDepartmentById(Long id) {
        if(!departmentRepository.existsById(id)){
            throw new ResourceNotFoundException("Department not found: " + id);
        }
        departmentRepository.deleteById(id);
    }
}
