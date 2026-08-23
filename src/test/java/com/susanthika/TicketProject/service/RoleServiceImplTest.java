package com.susanthika.TicketProject.service;

import com.susanthika.TicketProject.dto.request.RoleRequest;
import com.susanthika.TicketProject.dto.response.RoleResponse;
import com.susanthika.TicketProject.entity.Role;
import com.susanthika.TicketProject.exception.ResourceNotFoundException;
import com.susanthika.TicketProject.repository.RoleRepository;
import com.susanthika.TicketProject.service.impl.RoleServiceImpl;
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
public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleServiceImpl roleServiceImpl;


    @Test
    void findRoleById_shouldReturnRole_whenRoleExists() {

        Long id = 1L;

        Role role = new Role();
        RoleResponse roleResponse = new RoleResponse();

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        when(modelMapper.map(role, RoleResponse.class)).thenReturn(roleResponse);

        RoleResponse result = roleServiceImpl.findRoleById(id);

        assertNotNull(result);
        assertEquals(roleResponse, result);

        verify(roleRepository).findById(id);
        verify(modelMapper).map(role, RoleResponse.class);
    }


    @Test
    void findRoleById_shouldThrowException_whenRoleDoesNotExist() {

        Long id = 1L;

        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleServiceImpl.findRoleById(id)
        );

        verify(roleRepository).findById(id);

        verify(modelMapper, never()).map(any(), eq(RoleResponse.class));
    }


    @Test
    void findAllRoles_shouldReturnRoles() {

        Role role1 = new Role();
        role1.setId(1L);

        Role role2 = new Role();
        role2.setId(2L);

        RoleResponse response1 = new RoleResponse();
        RoleResponse response2 = new RoleResponse();

        when(roleRepository.findAll()).thenReturn(List.of(role1, role2));

        when(modelMapper.map(role1, RoleResponse.class)).thenReturn(response1);

        when(modelMapper.map(role2, RoleResponse.class)).thenReturn(response2);

        List<RoleResponse> result = roleServiceImpl.findAllRoles();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(roleRepository).findAll();

        verify(modelMapper).map(role1, RoleResponse.class);
        verify(modelMapper).map(role2, RoleResponse.class);
    }


    @Test
    void findAllRoles_shouldReturnEmptyList_whenNoRolesExist() {

        when(roleRepository.findAll()).thenReturn(List.of());

        List<RoleResponse> result = roleServiceImpl.findAllRoles();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(roleRepository).findAll();
    }


    @Test
    void createRole_shouldCreateAndReturnRole() {

        RoleRequest request = new RoleRequest();

        Role role = new Role();
        Role savedRole = new Role();
        RoleResponse response = new RoleResponse();

        when(modelMapper.map(request, Role.class)).thenReturn(role);

        when(roleRepository.save(role)).thenReturn(savedRole);

        when(modelMapper.map(savedRole, RoleResponse.class)).thenReturn(response);

        RoleResponse result = roleServiceImpl.createRole(request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(modelMapper).map(request, Role.class);
        verify(roleRepository).save(role);
        verify(modelMapper).map(savedRole, RoleResponse.class);
    }


    @Test
    void updateRole_shouldUpdateAndReturnRole_whenRoleExists() {

        Long id = 1L;

        RoleRequest request = new RoleRequest();

        Role existingRole = new Role();
        RoleResponse response = new RoleResponse();

        when(roleRepository.findById(id)).thenReturn(Optional.of(existingRole));

        doNothing().when(modelMapper).map(request, existingRole);

        when(roleRepository.save(existingRole)).thenReturn(existingRole);

        when(modelMapper.map(existingRole, RoleResponse.class)).thenReturn(response);

        RoleResponse result = roleServiceImpl.updateRole(id, request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(roleRepository).findById(id);
        verify(modelMapper).map(request, existingRole);
        verify(roleRepository).save(existingRole);
        verify(modelMapper).map(existingRole, RoleResponse.class);
    }


    @Test
    void updateRole_shouldThrowException_whenRoleDoesNotExist() {

        Long id = 1L;

        RoleRequest request = new RoleRequest();

        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleServiceImpl.updateRole(id, request)
        );

        verify(roleRepository).findById(id);

        verify(roleRepository, never()).save(any());
    }


    @Test
    void deleteRoleById_shouldDeleteRole_whenRoleExists() {

        Long id = 1L;

        when(roleRepository.existsById(id)).thenReturn(true);

        roleServiceImpl.deleteRoleById(id);

        verify(roleRepository).existsById(id);
        verify(roleRepository).deleteById(id);
    }


    @Test
    void deleteRoleById_shouldThrowException_whenRoleDoesNotExist() {

        Long id = 1L;

        when(roleRepository.existsById(id)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleServiceImpl.deleteRoleById(id)
        );

        verify(roleRepository).existsById(id);
        verify(roleRepository, never()).deleteById(id);
    }
}
