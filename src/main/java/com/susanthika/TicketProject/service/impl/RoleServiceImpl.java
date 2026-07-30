package com.susanthika.TicketProject.service.impl;

import com.susanthika.TicketProject.dto.request.RoleRequest;
import com.susanthika.TicketProject.dto.response.RoleResponse;
import com.susanthika.TicketProject.entity.Role;
import com.susanthika.TicketProject.exception.ResourceNotFoundException;
import com.susanthika.TicketProject.repository.RoleRepository;
import com.susanthika.TicketProject.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Override
    public RoleResponse findRoleById(Long id) {
        Role roleResponse = roleRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Role not found"));
        return modelMapper.map(roleResponse, RoleResponse.class);
    }

    @Override
    public List<RoleResponse> findAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(role -> modelMapper.map(role, RoleResponse.class))
                .toList(); //.collect(Collectors.toList());
    }

    @Override
    public RoleResponse createRole(RoleRequest roleRequest) {
        Role role = modelMapper.map(roleRequest, Role.class);
        Role saved = roleRepository.save(role);
        return modelMapper.map(saved, RoleResponse.class);
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest roleRequest) {
        Role existingRole = roleRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Role not found"));
        modelMapper.map(roleRequest, existingRole);
        Role updatedRole = roleRepository.save(existingRole);
        return modelMapper.map(updatedRole, RoleResponse.class);
    }

    @Override
    public void deleteRoleById(Long id) {
        if(!roleRepository.existsById(id)){
            throw new ResourceNotFoundException("Role not found: " + id);
        }
        roleRepository.deleteById(id);
    }
}
