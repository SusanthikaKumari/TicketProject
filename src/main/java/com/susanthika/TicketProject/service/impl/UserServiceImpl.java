package com.susanthika.TicketProject.service.impl;

import com.susanthika.TicketProject.dto.request.UserRequest;
import com.susanthika.TicketProject.dto.request.UserUpdateRequest;
import com.susanthika.TicketProject.dto.response.UserResponse;
import com.susanthika.TicketProject.entity.Department;
import com.susanthika.TicketProject.entity.Role;
import com.susanthika.TicketProject.entity.User;
import com.susanthika.TicketProject.exception.ResourceNotFoundException;
import com.susanthika.TicketProject.repository.DepartmentRepository;
import com.susanthika.TicketProject.repository.RoleRepository;
import com.susanthika.TicketProject.repository.UserRepository;
import com.susanthika.TicketProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public UserResponse findUserById(Long id) {
        User userResponse = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return mapToResponse(userResponse);
    }

    @Override
    public List<UserResponse> findAllUsers() {
//        return userRepository.findAll().stream()
//                .map(user -> modelMapper.map(user, UserResponse.class))
//                .collect(Collectors.toList()); // .toList());
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        String email = userRequest.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        Role role = roleRepository.findById(userRequest.getRoleId())
                .orElseThrow(()->new ResourceNotFoundException("Role not found"));

        Department department = null;

        if (userRequest.getDepartmentId() !=null){
            department = departmentRepository.findById(userRequest.getDepartmentId())
                    .orElseThrow(()->new ResourceNotFoundException("Department not found"));
        }

        if ("CUSTOMER".equals(role.getRoleName())){
            department = null;
        }else if (department == null){
            throw new IllegalArgumentException("Department is required for internal users");
        }

        User user = modelMapper.map(userRequest, User.class);
        user.setRole(role);
        user.setDepartment(department);
        user.setEmail(email);
        //user.setCreatedAt(LocalDateTime.now());
        //user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);

    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User userdb = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found: " + id));

        modelMapper.map(userUpdateRequest, userdb);
        User updatedUser = userRepository.save(userdb);
        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user){
        UserResponse response = modelMapper.map(user, UserResponse.class);
        response.setRole(user.getRole().getRoleName());

        if (user.getDepartment() != null){
            response.setDepartment(user.getDepartment().getDepartmentName());
        }
        return response;
    }

}