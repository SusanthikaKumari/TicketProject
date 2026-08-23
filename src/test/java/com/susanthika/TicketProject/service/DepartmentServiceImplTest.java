package com.susanthika.TicketProject.service;

import com.susanthika.TicketProject.dto.request.DepartmentRequest;
import com.susanthika.TicketProject.dto.response.DepartmentResponse;
import com.susanthika.TicketProject.entity.Department;
import com.susanthika.TicketProject.exception.ResourceNotFoundException;
import com.susanthika.TicketProject.repository.DepartmentRepository;
import com.susanthika.TicketProject.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentServiceImpl;


    @Test
    void findDepartmentById_shouldReturnDepartment_whenDepartmentExists() {

        Long id = 1L;

        Department department = new Department();
        DepartmentResponse departmentResponse = new DepartmentResponse();

        when(departmentRepository.findById(id))
                .thenReturn(Optional.of(department));

        when(modelMapper.map(department, DepartmentResponse.class))
                .thenReturn(departmentResponse);

        DepartmentResponse result = departmentServiceImpl.findDepartmentById(id);

        assertNotNull(result);
        assertEquals(departmentResponse, result);

        verify(departmentRepository).findById(id);
        verify(modelMapper).map(department, DepartmentResponse.class);
    }


    @Test
    void findDepartmentById_shouldThrowException_whenDepartmentDoesNotExist() {

        Long id = 1L;

        when(departmentRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> departmentServiceImpl.findDepartmentById(id)
        );

        verify(departmentRepository).findById(id);
        verify(modelMapper, never())
                .map(any(), eq(DepartmentResponse.class));
    }

    @Test
    void findAllDepartments_shouldReturnDepartments() {

        Department department1 = new Department();
        department1.setId(1L);
            department1.setDepartmentName("BACKEND");

        Department department2 = new Department();
        department2.setId(2L);
        department2.setDepartmentName("FRONTEND");

        DepartmentResponse response1 = new DepartmentResponse();
        DepartmentResponse response2 = new DepartmentResponse();

        when(departmentRepository.findAll())
                .thenReturn(List.of(department1, department2));

        when(modelMapper.map(department1, DepartmentResponse.class))
                .thenReturn(response1);

        when(modelMapper.map(department2, DepartmentResponse.class))
                .thenReturn(response2);

        List<DepartmentResponse> result =
                departmentServiceImpl.findAllDepartments();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(departmentRepository).findAll();

        verify(modelMapper).map(department1, DepartmentResponse.class);
        verify(modelMapper).map(department2, DepartmentResponse.class);
    }

    @Test
    void findAllDepartments_shouldReturnEmptyList_whenNoDepartmentsExist() {

        when(departmentRepository.findAll())
                .thenReturn(List.of());

        List<DepartmentResponse> result =
                departmentServiceImpl.findAllDepartments();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(departmentRepository).findAll();
    }


    @Test
    void createDepartment_shouldCreateAndReturnDepartment() {

        DepartmentRequest request = new DepartmentRequest();

        Department department = new Department();
        Department savedDepartment = new Department();
        DepartmentResponse response = new DepartmentResponse();

        when(modelMapper.map(request, Department.class))
                .thenReturn(department);

        when(departmentRepository.save(department))
                .thenReturn(savedDepartment);

        when(modelMapper.map(savedDepartment, DepartmentResponse.class))
                .thenReturn(response);

        DepartmentResponse result =
                departmentServiceImpl.createDepartment(request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(modelMapper).map(request, Department.class);
        verify(departmentRepository).save(department);
        verify(modelMapper).map(savedDepartment, DepartmentResponse.class);
    }


    @Test
    void updateDepartment_shouldUpdateAndReturnDepartment_whenDepartmentExists() {

        Long id = 1L;

        DepartmentRequest request = new DepartmentRequest();

        Department existingDepartment = new Department();
        DepartmentResponse response = new DepartmentResponse();

        when(departmentRepository.findById(id))
                .thenReturn(Optional.of(existingDepartment));

        doNothing().when(modelMapper)
                .map(request, existingDepartment);

        when(departmentRepository.save(existingDepartment))
                .thenReturn(existingDepartment);

        when(modelMapper.map(existingDepartment, DepartmentResponse.class))
                .thenReturn(response);

        DepartmentResponse result =
                departmentServiceImpl.updateDepartment(id, request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(departmentRepository).findById(id);
        verify(modelMapper).map(request, existingDepartment);
        verify(departmentRepository).save(existingDepartment);
        verify(modelMapper).map(existingDepartment, DepartmentResponse.class);
    }

    @Test
    void updateDepartment_shouldThrowException_whenDepartmentDoesNotExist() {

        Long id = 1L;
        DepartmentRequest request = new DepartmentRequest();

        when(departmentRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> departmentServiceImpl.updateDepartment(id, request)
        );

        verify(departmentRepository).findById(id);
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void deleteDepartmentById_shouldDeleteDepartment_whenDepartmentExists() {

        Long id = 1L;

        when(departmentRepository.existsById(id))
                .thenReturn(true);

        departmentServiceImpl.deleteDepartmentById(id);

        verify(departmentRepository).existsById(id);
        verify(departmentRepository).deleteById(id);
    }

    @Test
    void deleteDepartmentById_shouldThrowException_whenDepartmentDoesNotExist() {

        Long id = 1L;

        when(departmentRepository.existsById(id))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> departmentServiceImpl.deleteDepartmentById(id)
        );

        verify(departmentRepository).existsById(id);
        verify(departmentRepository, never()).deleteById(id);
    }
}
