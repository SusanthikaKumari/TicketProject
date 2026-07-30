package com.susanthika.TicketProject.controller;

import com.susanthika.TicketProject.dto.request.RoleRequest;
import com.susanthika.TicketProject.dto.response.RoleResponse;
import com.susanthika.TicketProject.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoleResponse getRoleById(@PathVariable Long id){
        return roleService.findRoleById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    private List<RoleResponse> getAllRoles(){
        return roleService.findAllRoles();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private RoleResponse createRole(@RequestBody RoleRequest roleRequest){
        return roleService.createRole(roleRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse updateRole(@PathVariable Long id, @RequestBody RoleRequest roleRequest){
        return roleService.updateRole(id, roleRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable Long id){
        roleService.deleteRoleById(id);
    }
}
