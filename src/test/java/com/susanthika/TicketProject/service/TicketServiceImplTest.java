package com.susanthika.TicketProject.service;

import com.susanthika.TicketProject.dto.request.TicketAdminUpdateRequest;
import com.susanthika.TicketProject.dto.request.TicketAssignRequest;
import com.susanthika.TicketProject.dto.request.TicketCustomerUpdateRequest;
import com.susanthika.TicketProject.dto.request.TicketRequest;
import com.susanthika.TicketProject.dto.response.TicketResponse;
import com.susanthika.TicketProject.entity.Department;
import com.susanthika.TicketProject.entity.Role;
import com.susanthika.TicketProject.entity.Ticket;
import com.susanthika.TicketProject.entity.User;
import com.susanthika.TicketProject.entity.enums.Status;
import com.susanthika.TicketProject.exception.BadRequestException;
import com.susanthika.TicketProject.exception.ResourceNotFoundException;
import com.susanthika.TicketProject.repository.DepartmentRepository;
import com.susanthika.TicketProject.repository.TicketRepository;
import com.susanthika.TicketProject.repository.UserRepository;
import com.susanthika.TicketProject.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {


    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TicketServiceImpl ticketServiceImpl;

    private void setAuthenticatedUser(String email) {

        Authentication authentication = mock(Authentication.class);

        when(authentication.getName())
                .thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }


    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createTicket_shouldCreateAndReturnTicket_whenCustomer() {

        TicketRequest request = new TicketRequest();
        request.setDepartmentId(1L);

        User customer = new User();
        customer.setId(1L);

        Role customerRole = mock(Role.class);
        customer.setRole(customerRole);

        Department department = new Department();
        department.setId(1L);
        department.setDepartmentName("IT");

        Ticket ticket = new Ticket();

        Ticket savedTicket = new Ticket();
        savedTicket.setId(1L);

        TicketResponse response = new TicketResponse();

        setAuthenticatedUser("customer@gmail.com");

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        when(customerRole.isCustomer())
                .thenReturn(true);

        when(departmentRepository.findById(1L))
                .thenReturn(Optional.of(department));

        when(modelMapper.map(request, Ticket.class))
                .thenReturn(ticket);

        when(ticketRepository.save(ticket))
                .thenReturn(savedTicket);

        when(ticketRepository.save(savedTicket))
                .thenReturn(savedTicket);

        when(modelMapper.map(savedTicket, TicketResponse.class))
                .thenReturn(response);

        TicketResponse result =
                ticketServiceImpl.createTicket(request);

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals(customer, ticket.getCustomer());
        assertEquals(department, ticket.getDepartment());
        assertEquals(Status.NEW, ticket.getStatus());

        assertEquals("TICKET-00001", savedTicket.getTicketCode());

        verify(userRepository).findByEmail("customer@gmail.com");
        verify(departmentRepository).findById(1L);
        verify(modelMapper).map(request, Ticket.class);

        verify(ticketRepository).save(ticket);
        verify(ticketRepository).save(savedTicket);

        verify(modelMapper).map(savedTicket, TicketResponse.class);
    }

    @Test
    void createTicket_shouldThrowException_whenUserIsNotCustomer() {

        TicketRequest request = new TicketRequest();
        request.setDepartmentId(1L);

        User admin = new User();

        Role adminRole = mock(Role.class);
        admin.setRole(adminRole);

        setAuthenticatedUser("admin@gmail.com");

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(admin));

        when(adminRole.isCustomer())
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.createTicket(request)
        );

        verify(userRepository).findByEmail("admin@gmail.com");

        verify(departmentRepository, never())
                .findById(any());

        verify(ticketRepository, never())
                .save(any());
    }


    @Test
    void createTicket_shouldThrowException_whenDepartmentDoesNotExist() {

        TicketRequest request = new TicketRequest();
        request.setDepartmentId(10L);

        User customer = new User();

        Role customerRole = mock(Role.class);
        customer.setRole(customerRole);

        setAuthenticatedUser("customer@gmail.com");

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        when(customerRole.isCustomer())
                .thenReturn(true);

        when(departmentRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketServiceImpl.createTicket(request)
        );

        verify(userRepository).findByEmail("customer@gmail.com");
        verify(departmentRepository).findById(10L);

        verify(modelMapper, never())
                .map(any(), eq(Ticket.class));

        verify(ticketRepository, never())
                .save(any());
    }


    @Test
    void findTicketByTicketCode_shouldReturnTicket_whenTicketExists() {

        String ticketCode = "TICKET-00001";

        Ticket ticket = new Ticket();

        TicketResponse response = new TicketResponse();

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(modelMapper.map(ticket, TicketResponse.class))
                .thenReturn(response);

        TicketResponse result =
                ticketServiceImpl.findTicketByTicketCode(ticketCode);

        assertNotNull(result);
        assertEquals(response, result);

        verify(ticketRepository)
                .findTicketByTicketCode(ticketCode);

        verify(modelMapper)
                .map(ticket, TicketResponse.class);
    }



    @Test
    void findTicketByTicketCode_shouldThrowException_whenTicketDoesNotExist() {

        String ticketCode = "TICKET-00001";

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketServiceImpl.findTicketByTicketCode(ticketCode)
        );

        verify(ticketRepository)
                .findTicketByTicketCode(ticketCode);

        verify(modelMapper, never())
                .map(any(), eq(TicketResponse.class));
    }


    @Test
    void assignTicketToAdmin_shouldAssignAdmin_whenValid() {

        String ticketCode = "TICKET-00001";

        TicketAssignRequest request = new TicketAssignRequest();
        request.setAdminId(2L);

        Department department = new Department();
        department.setId(1L);

        Role adminRole = mock(Role.class);

        User admin = new User();
        admin.setId(2L);
        admin.setRole(adminRole);
        admin.setDepartment(department);

        Ticket ticket = new Ticket();
        ticket.setDepartment(department);

        TicketResponse response = new TicketResponse();

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(admin));

        when(adminRole.isAdmin())
                .thenReturn(true);

        when(ticketRepository.save(ticket))
                .thenReturn(ticket);

        when(modelMapper.map(ticket, TicketResponse.class))
                .thenReturn(response);

        TicketResponse result =
                ticketServiceImpl.assignTicketToAdmin(ticketCode, request);

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals(admin, ticket.getAssignedAdminAgent());
        assertEquals(Status.IN_PROGRESS, ticket.getStatus());

        verify(ticketRepository)
                .findTicketByTicketCode(ticketCode);

        verify(userRepository)
                .findById(2L);

        verify(ticketRepository)
                .save(ticket);

        verify(modelMapper)
                .map(ticket, TicketResponse.class);
    }


    @Test
    void assignTicketToAdmin_shouldThrowException_whenAlreadyAssigned() {

        String ticketCode = "TICKET-00001";

        TicketAssignRequest request = new TicketAssignRequest();
        request.setAdminId(2L);

        Ticket ticket = new Ticket();

        User existingAdmin = new User();

        ticket.setAssignedAdminAgent(existingAdmin);

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.assignTicketToAdmin(ticketCode, request)
        );

        verify(ticketRepository)
                .findTicketByTicketCode(ticketCode);

        verify(userRepository, never())
                .findById(any());

        verify(ticketRepository, never())
                .save(any());
    }


    @Test
    void assignTicketToAdmin_shouldThrowException_whenAdminDoesNotExist() {

        String ticketCode = "TICKET-00001";

        TicketAssignRequest request = new TicketAssignRequest();
        request.setAdminId(2L);

        Ticket ticket = new Ticket();

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketServiceImpl.assignTicketToAdmin(ticketCode, request)
        );

        verify(ticketRepository)
                .findTicketByTicketCode(ticketCode);

        verify(userRepository)
                .findById(2L);

        verify(ticketRepository, never())
                .save(any());
    }


    @Test
    void assignTicketToAdmin_shouldThrowException_whenUserIsNotAdmin() {

        String ticketCode = "TICKET-00001";

        TicketAssignRequest request = new TicketAssignRequest();
        request.setAdminId(2L);

        Ticket ticket = new Ticket();

        Role customerRole = mock(Role.class);

        User user = new User();
        user.setRole(customerRole);

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        when(customerRole.isAdmin())
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.assignTicketToAdmin(ticketCode, request)
        );

        verify(userRepository).findById(2L);

        verify(ticketRepository, never())
                .save(any());
    }

    @Test
    void updateTicketByCustomer_shouldUpdateTicket_whenValid() {

        String ticketCode = "TICKET-00001";

        TicketCustomerUpdateRequest request =
                new TicketCustomerUpdateRequest();

        request.setTitle("Updated title");
        request.setDescription("Updated description");

        User customer = new User();
        customer.setId(1L);

        Role customerRole = mock(Role.class);
        customer.setRole(customerRole);

        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);

        TicketResponse response = new TicketResponse();

        setAuthenticatedUser("customer@gmail.com");

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        when(customerRole.isCustomer())
                .thenReturn(true);

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(ticketRepository.save(ticket))
                .thenReturn(ticket);

        when(modelMapper.map(ticket, TicketResponse.class))
                .thenReturn(response);

        TicketResponse result =
                ticketServiceImpl.updateTicketByCustomer(
                        ticketCode,
                        request
                );

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals("Updated title", ticket.getTitle());
        assertEquals("Updated description", ticket.getDescription());

        verify(ticketRepository)
                .findTicketByTicketCode(ticketCode);

        verify(userRepository)
                .findByEmail("customer@gmail.com");

        verify(ticketRepository)
                .save(ticket);

        verify(modelMapper)
                .map(ticket, TicketResponse.class);
    }

    @Test
    void updateTicketByCustomer_shouldThrowException_whenTicketBelongsToAnotherCustomer() {

        String ticketCode = "TICKET-00001";

        TicketCustomerUpdateRequest request =
                new TicketCustomerUpdateRequest();

        User ticketOwner = new User();
        ticketOwner.setId(1L);

        User currentCustomer = new User();
        currentCustomer.setId(2L);

        Role customerRole = mock(Role.class);
        currentCustomer.setRole(customerRole);

        Ticket ticket = new Ticket();
        ticket.setCustomer(ticketOwner);

        setAuthenticatedUser("customer2@gmail.com");

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("customer2@gmail.com"))
                .thenReturn(Optional.of(currentCustomer));

        when(customerRole.isCustomer())
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.updateTicketByCustomer(
                        ticketCode,
                        request
                )
        );

        verify(ticketRepository)
                .findTicketByTicketCode(ticketCode);

        verify(userRepository)
                .findByEmail("customer2@gmail.com");

        verify(ticketRepository, never())
                .save(any());
    }


    @Test
    void updateTicketByCustomer_shouldThrowException_whenTicketAlreadyAssigned() {

        String ticketCode = "TICKET-00001";

        TicketCustomerUpdateRequest request =
                new TicketCustomerUpdateRequest();

        User customer = new User();
        customer.setId(1L);

        Role customerRole = mock(Role.class);
        customer.setRole(customerRole);

        User admin = new User();

        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setAssignedAdminAgent(admin);

        setAuthenticatedUser("customer@gmail.com");

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        when(customerRole.isCustomer())
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.updateTicketByCustomer(
                        ticketCode,
                        request
                )
        );

        verify(ticketRepository, never())
                .save(any());
    }

    @Test
    void updateTicketStatusByAdmin_shouldUpdateStatus_whenValid() {

        String ticketCode = "TICKET-00001";

        TicketAdminUpdateRequest request =
                new TicketAdminUpdateRequest();

        request.setStatus(Status.COMPLETED);

        Role adminRole = mock(Role.class);

        User admin = new User();
        admin.setId(2L);
        admin.setRole(adminRole);

        Ticket ticket = new Ticket();
        ticket.setAssignedAdminAgent(admin);

        TicketResponse response = new TicketResponse();

        setAuthenticatedUser("admin@gmail.com");

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(admin));

        when(adminRole.isAdmin())
                .thenReturn(true);

        when(ticketRepository.save(ticket))
                .thenReturn(ticket);

        when(modelMapper.map(ticket, TicketResponse.class))
                .thenReturn(response);

        TicketResponse result =
                ticketServiceImpl.updateTicketStatusByAdmin(
                        ticketCode,
                        request
                );

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals(Status.COMPLETED, ticket.getStatus());

        verify(ticketRepository)
                .findTicketByTicketCode(ticketCode);

        verify(userRepository)
                .findByEmail("admin@gmail.com");

        verify(ticketRepository)
                .save(ticket);

        verify(modelMapper)
                .map(ticket, TicketResponse.class);
    }


    @Test
    void updateTicketStatusByAdmin_shouldThrowException_whenUserIsNotAdmin() {

        String ticketCode = "TICKET-00001";

        TicketAdminUpdateRequest request =
                new TicketAdminUpdateRequest();

        request.setStatus(Status.COMPLETED);

        Role customerRole = mock(Role.class);

        User customer = new User();
        customer.setId(1L);
        customer.setRole(customerRole);

        Ticket ticket = new Ticket();

        setAuthenticatedUser("customer@gmail.com");

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        when(customerRole.isAdmin())
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.updateTicketStatusByAdmin(
                        ticketCode,
                        request
                )
        );

        verify(ticketRepository, never())
                .save(any());
    }


    @Test
    void updateTicketStatusByAdmin_shouldThrowException_whenTicketNotAssigned() {

        String ticketCode = "TICKET-00001";

        TicketAdminUpdateRequest request =
                new TicketAdminUpdateRequest();

        request.setStatus(Status.COMPLETED);

        Role adminRole = mock(Role.class);

        User admin = new User();
        admin.setId(2L);
        admin.setRole(adminRole);

        Ticket ticket = new Ticket();
        ticket.setAssignedAdminAgent(null);

        setAuthenticatedUser("admin@gmail.com");

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(admin));

        when(adminRole.isAdmin())
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.updateTicketStatusByAdmin(
                        ticketCode,
                        request
                )
        );

        verify(ticketRepository, never())
                .save(any());
    }


    @Test
    void updateTicketStatusByAdmin_shouldThrowException_whenAssignedToAnotherAdmin() {

        String ticketCode = "TICKET-00001";

        TicketAdminUpdateRequest request =
                new TicketAdminUpdateRequest();

        request.setStatus(Status.COMPLETED);

        Role adminRole = mock(Role.class);

        User currentAdmin = new User();
        currentAdmin.setId(2L);
        currentAdmin.setRole(adminRole);

        User assignedAdmin = new User();
        assignedAdmin.setId(3L);

        Ticket ticket = new Ticket();
        ticket.setAssignedAdminAgent(assignedAdmin);

        setAuthenticatedUser("admin@gmail.com");

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(currentAdmin));

        when(adminRole.isAdmin())
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.updateTicketStatusByAdmin(
                        ticketCode,
                        request
                )
        );

        verify(ticketRepository, never())
                .save(any());
    }


    @Test
    void deleteTicketByTicketCode_shouldDeleteTicket_whenValidCustomer() {

        String ticketCode = "TICKET-00001";

        User customer = new User();
        customer.setId(1L);

        Role customerRole = mock(Role.class);
        customer.setRole(customerRole);

        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setAssignedAdminAgent(null);

        setAuthenticatedUser("customer@gmail.com");

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        when(customerRole.isCustomer())
                .thenReturn(true);

        ticketServiceImpl.deleteTicketByTicketCode(ticketCode);

        verify(ticketRepository)
                .findTicketByTicketCode(ticketCode);

        verify(userRepository)
                .findByEmail("customer@gmail.com");

        verify(ticketRepository)
                .delete(ticket);
    }


    @Test
    void deleteTicketByTicketCode_shouldThrowException_whenTicketAssigned() {

        String ticketCode = "TICKET-00001";

        User customer = new User();
        customer.setId(1L);

        Role customerRole = mock(Role.class);
        customer.setRole(customerRole);

        User admin = new User();

        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setAssignedAdminAgent(admin);

        setAuthenticatedUser("customer@gmail.com");

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        when(customerRole.isCustomer())
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.deleteTicketByTicketCode(ticketCode)
        );

        verify(ticketRepository, never())
                .delete(any());
    }


    @Test
    void findCustomerTickets_shouldThrowException_whenUserIsNotCustomer() {

        User admin = new User();

        Role adminRole = mock(Role.class);
        admin.setRole(adminRole);

        setAuthenticatedUser("admin@gmail.com");

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(admin));

        when(adminRole.isCustomer())
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.findCustomerTickets()
        );

        verify(ticketRepository, never())
                .findByCustomerId(any());
    }


    @Test
    void findTicketsByAdminId_shouldThrowException_whenUserIsNotManager() {

        Long adminId = 2L;

        Role customerRole = mock(Role.class);

        User customer = new User();
        customer.setRole(customerRole);

        setAuthenticatedUser("customer@gmail.com");

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        when(customerRole.isManager())
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.findTicketsByAdminId(adminId)
        );

        verify(userRepository, never())
                .findById(adminId);

        verify(ticketRepository, never())
                .findByAssignedAdminAgentId(any());
    }

    @Test
    void findTicketsByAdminId_shouldThrowException_whenAdminDoesNotExist() {

        Long adminId = 2L;

        Role managerRole = mock(Role.class);

        User manager = new User();
        manager.setRole(managerRole);

        setAuthenticatedUser("manager@gmail.com");

        when(userRepository.findByEmail("manager@gmail.com"))
                .thenReturn(Optional.of(manager));

        when(managerRole.isManager())
                .thenReturn(true);

        when(userRepository.findById(adminId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> ticketServiceImpl.findTicketsByAdminId(adminId)
        );

        verify(ticketRepository, never())
                .findByAssignedAdminAgentId(any());
    }

    @Test
    void findTicketsByAdminId_shouldThrowException_whenSelectedUserIsNotAdmin() {

        Long adminId = 2L;

        Role managerRole = mock(Role.class);

        User manager = new User();
        manager.setRole(managerRole);

        Role customerRole = mock(Role.class);

        User customer = new User();
        customer.setRole(customerRole);

        setAuthenticatedUser("manager@gmail.com");

        when(userRepository.findByEmail("manager@gmail.com"))
                .thenReturn(Optional.of(manager));

        when(managerRole.isManager())
                .thenReturn(true);

        when(userRepository.findById(adminId))
                .thenReturn(Optional.of(customer));

        when(customerRole.isAdmin())
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.findTicketsByAdminId(adminId)
        );

        verify(ticketRepository, never())
                .findByAssignedAdminAgentId(any());
    }


    @Test
    void findAllTicketsByManager_shouldThrowException_whenUserIsNotManager() {

        Role adminRole = mock(Role.class);

        User admin = new User();
        admin.setRole(adminRole);

        setAuthenticatedUser("admin@gmail.com");

        when(userRepository.findByEmail("admin@gmail.com"))
                .thenReturn(Optional.of(admin));

        when(adminRole.isManager())
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> ticketServiceImpl.findAllTicketsByManager()
        );

        verify(ticketRepository, never())
                .findAll();
    }


    @Test
    void findCustomerTickets_shouldReturnTickets_whenCustomer() {

        User customer = new User();
        customer.setId(1L);

        Role customerRole = mock(Role.class);
        customer.setRole(customerRole);

        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);

        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);

        TicketResponse response1 = new TicketResponse();
        TicketResponse response2 = new TicketResponse();

        setAuthenticatedUser("customer@gmail.com");

        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));

        when(customerRole.isCustomer())
                .thenReturn(true);

        when(ticketRepository.findByCustomerId(1L))
                .thenReturn(List.of(ticket1, ticket2));

        when(modelMapper.map(ticket1, TicketResponse.class))
                .thenReturn(response1);

        when(modelMapper.map(ticket2, TicketResponse.class))
                .thenReturn(response2);

        List<TicketResponse> result =
                ticketServiceImpl.findCustomerTickets();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(userRepository)
                .findByEmail("customer@gmail.com");

        verify(ticketRepository)
                .findByCustomerId(1L);

        verify(modelMapper)
                .map(ticket1, TicketResponse.class);

        verify(modelMapper)
                .map(ticket2, TicketResponse.class);
    }

    @Test
    void findAllAdminTickets_shouldReturnAllTickets() {

        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);

        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);

        TicketResponse response1 = new TicketResponse();
        TicketResponse response2 = new TicketResponse();

        when(ticketRepository.findAll())
                .thenReturn(List.of(ticket1, ticket2));

        when(modelMapper.map(ticket1, TicketResponse.class))
                .thenReturn(response1);

        when(modelMapper.map(ticket2, TicketResponse.class))
                .thenReturn(response2);

        List<TicketResponse> result =
                ticketServiceImpl.findAllAdminTickets();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(ticketRepository)
                .findAll();

        verify(modelMapper)
                .map(ticket1, TicketResponse.class);

        verify(modelMapper)
                .map(ticket2, TicketResponse.class);
    }

    @Test
    void findTicketsByAdminId_shouldReturnTickets_whenManager() {

        Long adminId = 2L;

        Role managerRole = mock(Role.class);

        User manager = new User();
        manager.setId(1L);
        manager.setRole(managerRole);

        Role adminRole = mock(Role.class);

        User admin = new User();
        admin.setId(adminId);
        admin.setRole(adminRole);

        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);

        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);

        TicketResponse response1 = new TicketResponse();
        TicketResponse response2 = new TicketResponse();

        setAuthenticatedUser("manager@gmail.com");

        when(userRepository.findByEmail("manager@gmail.com"))
                .thenReturn(Optional.of(manager));

        when(managerRole.isManager())
                .thenReturn(true);

        when(userRepository.findById(adminId))
                .thenReturn(Optional.of(admin));

        when(adminRole.isAdmin())
                .thenReturn(true);

        when(ticketRepository.findByAssignedAdminAgentId(adminId))
                .thenReturn(List.of(ticket1, ticket2));

        when(modelMapper.map(ticket1, TicketResponse.class))
                .thenReturn(response1);

        when(modelMapper.map(ticket2, TicketResponse.class))
                .thenReturn(response2);

        List<TicketResponse> result =
                ticketServiceImpl.findTicketsByAdminId(adminId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(userRepository)
                .findByEmail("manager@gmail.com");

        verify(userRepository)
                .findById(adminId);

        verify(ticketRepository)
                .findByAssignedAdminAgentId(adminId);

        verify(modelMapper)
                .map(ticket1, TicketResponse.class);

        verify(modelMapper)
                .map(ticket2, TicketResponse.class);
    }

    @Test
    void findAllTicketsByManager_shouldReturnTickets_whenManager() {

        Role managerRole = mock(Role.class);

        User manager = new User();
        manager.setRole(managerRole);

        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);

        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);

        TicketResponse response1 = new TicketResponse();
        TicketResponse response2 = new TicketResponse();

        setAuthenticatedUser("manager@gmail.com");

        when(userRepository.findByEmail("manager@gmail.com"))
                .thenReturn(Optional.of(manager));

        when(managerRole.isManager())
                .thenReturn(true);

        when(ticketRepository.findAll())
                .thenReturn(List.of(ticket1, ticket2));

        when(modelMapper.map(ticket1, TicketResponse.class))
                .thenReturn(response1);

        when(modelMapper.map(ticket2, TicketResponse.class))
                .thenReturn(response2);

        List<TicketResponse> result =
                ticketServiceImpl.findAllTicketsByManager();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(userRepository)
                .findByEmail("manager@gmail.com");

        verify(ticketRepository)
                .findAll();

        verify(modelMapper)
                .map(ticket1, TicketResponse.class);

        verify(modelMapper)
                .map(ticket2, TicketResponse.class);
    }

}