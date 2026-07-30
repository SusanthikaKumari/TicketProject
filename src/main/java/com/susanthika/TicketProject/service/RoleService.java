package com.susanthika.TicketProject.service;

import com.susanthika.TicketProject.dto.request.RoleRequest;
import com.susanthika.TicketProject.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse findRoleById(Long id);
    List<RoleResponse> findAllRoles();
    RoleResponse createRole(RoleRequest roleRequest);
    RoleResponse updateRole(Long id, RoleRequest roleRequest);
    void deleteRoleById(Long id);
}
