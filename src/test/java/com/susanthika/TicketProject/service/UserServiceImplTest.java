package com.susanthika.TicketProject.service;

import com.susanthika.TicketProject.dto.request.UserRequest;
import com.susanthika.TicketProject.dto.request.UserUpdateRequest;
import com.susanthika.TicketProject.dto.response.UserResponse;
import com.susanthika.TicketProject.entity.Department;
import com.susanthika.TicketProject.entity.Role;
import com.susanthika.TicketProject.entity.User;
import com.susanthika.TicketProject.exception.BadRequestException;
import com.susanthika.TicketProject.exception.DuplicateResourceException;
import com.susanthika.TicketProject.exception.ResourceNotFoundException;
import com.susanthika.TicketProject.repository.DepartmentRepository;
import com.susanthika.TicketProject.repository.RoleRepository;
import com.susanthika.TicketProject.repository.UserRepository;
import com.susanthika.TicketProject.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {


    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;


    @Test
    void findUserById_shouldReturnUser_whenUserExists() {

        Long id = 1L;

        User user = new User();
        Role role = new Role();
        role.setRoleName("CUSTOMER");
        user.setRole(role);

        UserResponse response = new UserResponse();

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        when(modelMapper.map(user, UserResponse.class))
                .thenReturn(response);

        UserResponse result = userServiceImpl.findUserById(id);

        assertNotNull(result);
        assertEquals(response, result);

        verify(userRepository).findById(id);
        verify(modelMapper).map(user, UserResponse.class);
    }


    @Test
    void findUserById_shouldThrowException_whenUserDoesNotExist() {

        Long id = 1L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userServiceImpl.findUserById(id)
        );

        verify(userRepository).findById(id);

        verify(modelMapper, never()).map(any(), eq(UserResponse.class));
    }


    @Test
    void findAllUsers_shouldReturnUsers() {

        User user1 = new User();
        User user2 = new User();

        Role role1 = new Role();
        role1.setRoleName("CUSTOMER");

        Role role2 = new Role();
        role2.setRoleName("ADMIN");

        user1.setRole(role1);
        user2.setRole(role2);

        UserResponse response1 = new UserResponse();
        UserResponse response2 = new UserResponse();

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        when(modelMapper.map(user1, UserResponse.class))
                .thenReturn(response1);

        when(modelMapper.map(user2, UserResponse.class))
                .thenReturn(response2);

        List<UserResponse> result = userServiceImpl.findAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());  // check there are two responses

        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(userRepository).findAll();

        verify(modelMapper).map(user1, UserResponse.class);
        verify(modelMapper).map(user2, UserResponse.class);
    }


    @Test
    void findAllUsers_shouldReturnEmptyList_whenNoUsersExist() {

        when(userRepository.findAll())
                .thenReturn(List.of());

        List<UserResponse> result =
                userServiceImpl.findAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
    }


    @Test
    void createUser_shouldCreateCustomer_whenValidRequest() {

        UserRequest request = new UserRequest();
        request.setEmail("  Customer@Gmail.COM  ");
        request.setPassword("password");
        request.setRoleId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setRoleName("CUSTOMER");

        User user = new User();
        User savedUser = new User();

        savedUser.setRole(role);

        UserResponse response = new UserResponse();

        when(userRepository.existsByEmail("customer@gmail.com"))
                .thenReturn(false);

        when(roleRepository.findById(1L))
                .thenReturn(Optional.of(role));

        when(modelMapper.map(request, User.class))
                .thenReturn(user);

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        when(userRepository.save(user))
                .thenReturn(savedUser);

        when(modelMapper.map(savedUser, UserResponse.class))
                .thenReturn(response);

        UserResponse result =
                userServiceImpl.createUser(request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(userRepository).existsByEmail("customer@gmail.com");
        verify(roleRepository).findById(1L);
        verify(modelMapper).map(request, User.class);
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);
        verify(modelMapper).map(savedUser, UserResponse.class);

        assertEquals("encodedPassword", user.getPassword());
        assertEquals(role, user.getRole());
        assertNull(user.getDepartment());
        assertEquals("customer@gmail.com", user.getEmail());
        assertTrue(user.isEnabled());
    }


    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {

        UserRequest request = new UserRequest();
        request.setEmail("customernew@gmail.com");
        request.setPassword("password");
        request.setRoleId(1L);

        when(userRepository.existsByEmail("customernew@gmail.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> userServiceImpl.createUser(request)
        );

        verify(userRepository).existsByEmail("customernew@gmail.com");

        verify(roleRepository, never())
                .findById(any());

        verify(userRepository, never())
                .save(any());
    }


    @Test
    void createUser_shouldThrowException_whenRoleDoesNotExist() {

        UserRequest request = new UserRequest();
        request.setEmail("customernew@gmail.com");
        request.setPassword("password");
        request.setRoleId(1L);

        when(userRepository.existsByEmail("customernew@gmail.com"))
                .thenReturn(false);

        when(roleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userServiceImpl.createUser(request)
        );

        verify(userRepository).existsByEmail("customernew@gmail.com");
        verify(roleRepository).findById(1L);

        verify(userRepository, never())
                .save(any());
    }


    @Test
    void createUser_shouldCreateInternalUser_whenDepartmentExists() {

        UserRequest request = new UserRequest();
        request.setEmail("admin@gmail.com");
        request.setPassword("password");
        request.setRoleId(2L);
        request.setDepartmentId(1L);

        Role role = new Role();
        role.setId(2L);
        role.setRoleName("ADMIN");

        Department department = new Department();
        department.setId(1L);
        department.setDepartmentName("BACKEND");

        User user = new User();
        User savedUser = new User();

        savedUser.setRole(role);
        savedUser.setDepartment(department);

        UserResponse response = new UserResponse();

        when(userRepository.existsByEmail("admin@gmail.com"))
                .thenReturn(false);

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(role));

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        when(modelMapper.map(request, User.class))
                .thenReturn(user);

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        when(userRepository.save(user))
                .thenReturn(savedUser);

        when(modelMapper.map(savedUser, UserResponse.class))
                .thenReturn(response);

        UserResponse result =
                userServiceImpl.createUser(request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(userRepository).existsByEmail("admin@gmail.com");
        verify(roleRepository).findById(2L);
        verify(departmentRepository).findById(1L);
        verify(modelMapper).map(request, User.class);
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);
        verify(modelMapper).map(savedUser, UserResponse.class);

        assertEquals(role, user.getRole());
        assertEquals(department, user.getDepartment());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("admin@gmail.com", user.getEmail());
        assertTrue(user.isEnabled());
    }


    @Test
    void createUser_shouldThrowException_whenDepartmentDoesNotExist() {

        UserRequest request = new UserRequest();
        request.setEmail("admin@gmail.com");
        request.setPassword("password");
        request.setRoleId(2L);
        request.setDepartmentId(1L);

        Role role = new Role();
        role.setRoleName("ADMIN");

        when(userRepository.existsByEmail("admin@gmail.com"))
                .thenReturn(false);

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(role));

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userServiceImpl.createUser(request)
        );

        verify(departmentRepository).findById(1L);

        verify(userRepository, never())
                .save(any());
    }


    @Test
    void createUser_shouldThrowException_whenInternalUserHasNoDepartment() {

        UserRequest request = new UserRequest();
        request.setEmail("admin@gmail.com");
        request.setPassword("password");
        request.setRoleId(2L);

        Role role = new Role();
        role.setRoleName("ADMIN");

        when(userRepository.existsByEmail("admin@gmail.com"))
                .thenReturn(false);

        when(roleRepository.findById(2L))
                .thenReturn(Optional.of(role));

        assertThrows(
                BadRequestException.class,
                () -> userServiceImpl.createUser(request)
        );

        verify(roleRepository).findById(2L);

        verify(departmentRepository, never())
                .findById(any());

        verify(userRepository, never())
                .save(any());
    }


    @Test
    void updateUser_shouldUpdateAndReturnUser_whenValidRequest() {

        Long id = 1L;

        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Kalana");
        request.setLastName("Pathum");
        request.setEmail("  KALANA@GMAIL.COM ");
        request.setPassword("newPassword");

        User existingUser = new User();

        Role role = new Role();
        role.setRoleName("CUSTOMER");

        existingUser.setRole(role);
        existingUser.setEmail("oldkalana@gmail.com");

        UserResponse response = new UserResponse();

        when(userRepository.findById(id))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.existsByEmail("kalana@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encodedPassword");

        when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        when(modelMapper.map(existingUser, UserResponse.class))
                .thenReturn(response);

        UserResponse result = userServiceImpl.updateUser(id, request);

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals("Kalana", existingUser.getFirstName());
        assertEquals("Pathum", existingUser.getLastName());
        assertEquals("kalana@gmail.com", existingUser.getEmail());
        assertEquals("encodedPassword", existingUser.getPassword());
        assertEquals(role, existingUser.getRole());
        assertNull(existingUser.getDepartment());

        verify(userRepository).findById(id);
        verify(userRepository).existsByEmail("kalana@gmail.com");
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(existingUser);
        verify(modelMapper).map(existingUser, UserResponse.class);
    }


    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExist() {

        Long id = 1L;

        UserUpdateRequest request = new UserUpdateRequest();

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userServiceImpl.updateUser(id, request)
        );

        verify(userRepository).findById(id);

        verify(userRepository, never())
                .save(any());
    }


    @Test
    void updateUser_shouldThrowException_whenNewEmailAlreadyExists() {

        Long id = 1L;

        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("new@gmail.com");

        User existingUser = new User();
        existingUser.setEmail("old@gmail.com");

        Role role = new Role();
        role.setRoleName("CUSTOMER");

        existingUser.setRole(role);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.existsByEmail("new@gmail.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> userServiceImpl.updateUser(id, request)
        );

        verify(userRepository).findById(id);
        verify(userRepository).existsByEmail("new@gmail.com");

        verify(userRepository, never())
                .save(any());
    }


    @Test
    void updateUser_shouldThrowException_whenNewRoleDoesNotExist() {

        Long id = 1L;

        UserUpdateRequest request = new UserUpdateRequest();
        request.setRoleId(5L);

        User existingUser = new User();

        Role existingRole = new Role();
        existingRole.setRoleName("CUSTOMER");

        existingUser.setRole(existingRole);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(existingUser));

        when(roleRepository.findById(5L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userServiceImpl.updateUser(id, request)
        );

        verify(userRepository).findById(id);
        verify(roleRepository).findById(5L);

        verify(userRepository, never())
                .save(any());
    }


    @Test
    void updateUser_shouldThrowException_whenNewDepartmentDoesNotExist() {

        Long id = 1L;

        UserUpdateRequest request = new UserUpdateRequest();
        request.setDepartmentId(5L);

        User existingUser = new User();

        Role role = new Role();
        role.setRoleName("ADMIN");

        existingUser.setRole(role);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(existingUser));

        when(departmentRepository.findById(5L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userServiceImpl.updateUser(id, request)
        );

        verify(userRepository).findById(id);
        verify(departmentRepository).findById(5L);

        verify(userRepository, never())
                .save(any());
    }

    @Test
    void deleteUserById_shouldDeleteUser_whenUserExists() {

        Long id = 1L;

        User user = new User();

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        userServiceImpl.deleteUserById(id);

        verify(userRepository).findById(id);
        verify(userRepository).delete(user);
    }


    @Test
    void deleteUserById_shouldThrowException_whenUserDoesNotExist() {

        Long id = 1L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userServiceImpl.deleteUserById(id)
        );

        verify(userRepository).findById(id);

        verify(userRepository, never())
                .delete(any());
    }
}
