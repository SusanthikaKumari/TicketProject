package com.susanthika.TicketProject.service;

import com.susanthika.TicketProject.dto.request.MessageRequest;
import com.susanthika.TicketProject.dto.request.MessageUpdateRequest;
import com.susanthika.TicketProject.dto.response.MessageResponse;
import com.susanthika.TicketProject.entity.Message;
import com.susanthika.TicketProject.entity.Role;
import com.susanthika.TicketProject.entity.Ticket;
import com.susanthika.TicketProject.entity.User;
import com.susanthika.TicketProject.exception.BadRequestException;
import com.susanthika.TicketProject.exception.ResourceNotFoundException;
import com.susanthika.TicketProject.repository.MessageRepository;
import com.susanthika.TicketProject.repository.TicketRepository;
import com.susanthika.TicketProject.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private MessageServiceImpl messageServiceImpl;


    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void sendCustomerMessage_shouldSendMessage_whenCustomerOwnsTicket() {

        MessageRequest request = new MessageRequest();
        request.setTicketCode("TICKET-00001");
        request.setMessage("I need help");

        User customer = new User();
        customer.setId(1L);
        customer.setFirstName("Akila");
        customer.setLastName("Customer");

        Role customerRole = mock(Role.class);
        customer.setRole(customerRole);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketCode("TICKET-00001");
        ticket.setCustomer(customer);

        Message message = new Message();

        Message savedMessage = new Message();
        savedMessage.setTicket(ticket);
        savedMessage.setSender(customer);

        MessageResponse response = new MessageResponse();

        when(ticketRepository.findTicketByTicketCode("TICKET-00001"))
                .thenReturn(Optional.of(ticket));

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(customer);

        when(customerRole.isCustomer())
                .thenReturn(true);

        when(customerRole.getRoleName())
                .thenReturn("CUSTOMER");

        when(modelMapper.map(request, Message.class))
                .thenReturn(message);

        when(messageRepository.save(message))
                .thenReturn(savedMessage);

        when(modelMapper.map(savedMessage, MessageResponse.class))
                .thenReturn(response);

        MessageResponse result =
                messageServiceImpl.sendCustomerMessage(request);

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals(ticket, message.getTicket());
        assertEquals(customer, message.getSender());

        verify(ticketRepository)
                .findTicketByTicketCode("TICKET-00001");

        verify(modelMapper)
                .map(request, Message.class);

        verify(messageRepository)
                .save(message);

        verify(modelMapper)
                .map(savedMessage, MessageResponse.class);
    }


    @Test
    void sendCustomerMessage_shouldThrowException_whenTicketNotFound() {

        MessageRequest request = new MessageRequest();
        request.setTicketCode("TICKET-01000");
        request.setMessage("I need help");

        when(ticketRepository.findTicketByTicketCode("TICKET-01000"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> messageServiceImpl.sendCustomerMessage(request)
        );

        verify(ticketRepository)
                .findTicketByTicketCode("TICKET-01000");

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(messageRepository);
    }


    @Test
    void sendCustomerMessage_shouldThrowException_whenUserIsNotCustomer() {

        MessageRequest request = new MessageRequest();
        request.setTicketCode("TICKET-00001");

        User user = new User();
        user.setId(1L);

        Role role = mock(Role.class);
        user.setRole(role);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketCode("TICKET-00001");

        ticket.setCustomer(user);

        when(ticketRepository.findTicketByTicketCode("TICKET-00001"))
                .thenReturn(Optional.of(ticket));

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(role.isCustomer())
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> messageServiceImpl.sendCustomerMessage(request)
        );

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(messageRepository);
    }


    @Test
    void sendCustomerMessage_shouldThrowException_whenTicketDoesNotBelongToCustomer() {

        MessageRequest request = new MessageRequest();
        request.setTicketCode("TICKET-00001");

        User ticketOwner = new User();
        ticketOwner.setId(2L);

        User currentCustomer = new User();
        currentCustomer.setId(1L);

        Role role = mock(Role.class);
        currentCustomer.setRole(role);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketCode("TICKET-00001");
        ticket.setCustomer(ticketOwner);

        when(ticketRepository.findTicketByTicketCode("TICKET-00001"))
                .thenReturn(Optional.of(ticket));

        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(currentCustomer);

        when(role.isCustomer()).thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> messageServiceImpl.sendCustomerMessage(request)
        );

        verify(ticketRepository).findTicketByTicketCode("TICKET-00001");

        verify(securityContext).getAuthentication();

        verify(authentication).getPrincipal();

        verify(role).isCustomer();

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(messageRepository);
    }


    @Test
    void sendAdminMessage_shouldSendMessage_whenAssignedAdmin() {

        MessageRequest request = new MessageRequest();
        request.setTicketCode("TICKET-00001");
        request.setMessage("We are working on your issue");

        User admin = new User();
        admin.setId(10L);
        admin.setFirstName("Ruwan");
        admin.setLastName("Admin");

        Role adminRole = mock(Role.class);
        admin.setRole(adminRole);

        User assignedAdmin = new User();
        assignedAdmin.setId(10L);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketCode("TICKET-00001");
        ticket.setAssignedAdminAgent(assignedAdmin);

        Message message = new Message();

        Message savedMessage = new Message();
        savedMessage.setTicket(ticket);
        savedMessage.setSender(admin);

        MessageResponse response = new MessageResponse();

        when(ticketRepository.findTicketByTicketCode("TICKET-00001"))
                .thenReturn(Optional.of(ticket));

        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(admin);

        when(adminRole.isAdmin()).thenReturn(true);

        when(adminRole.getRoleName()).thenReturn("ADMIN");

        when(modelMapper.map(request, Message.class)).thenReturn(message);

        when(messageRepository.save(message)).thenReturn(savedMessage);

        when(modelMapper.map(savedMessage, MessageResponse.class))
                .thenReturn(response);

        MessageResponse result = messageServiceImpl.sendAdminMessage(request);

        assertNotNull(result);
        assertEquals(response, result);

        assertEquals(ticket, message.getTicket());
        assertEquals(admin, message.getSender());

        verify(ticketRepository).findTicketByTicketCode("TICKET-00001");

        verify(securityContext).getAuthentication();

        verify(authentication).getPrincipal();

        verify(adminRole).isAdmin();

        verify(modelMapper).map(request, Message.class);

        verify(messageRepository).save(message);

        verify(modelMapper).map(savedMessage, MessageResponse.class);
    }


    @Test
    void sendAdminMessage_shouldThrowException_whenUserIsNotAdmin() {

        MessageRequest request = new MessageRequest();
        request.setTicketCode("TICKET-00001");

        User user = new User();
        user.setId(1L);

        Role role = mock(Role.class);
        user.setRole(role);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketCode("TICKET-00001");
        ticket.setAssignedAdminAgent(user);

        when(ticketRepository.findTicketByTicketCode("TICKET-00001"))
                .thenReturn(Optional.of(ticket));

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(user);

        when(role.isAdmin())
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> messageServiceImpl.sendAdminMessage(request)
        );

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(messageRepository);
    }


    @Test
    void sendAdminMessage_shouldThrowException_whenNoAdminAssigned() {

        MessageRequest request = new MessageRequest();
        request.setTicketCode("TICKET-00001");

        User admin = new User();
        admin.setId(10L);

        Role role = mock(Role.class);
        admin.setRole(role);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketCode("TICKET-00001");
        ticket.setAssignedAdminAgent(null);

        when(ticketRepository.findTicketByTicketCode("TICKET-00001"))
                .thenReturn(Optional.of(ticket));

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(admin);

        when(role.isAdmin())
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> messageServiceImpl.sendAdminMessage(request)
        );

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(messageRepository);
    }


    @Test
    void sendAdminMessage_shouldThrowException_whenAdminIsNotAssignedAdmin() {

        MessageRequest request = new MessageRequest();
        request.setTicketCode("TICKET-00001");

        User currentAdmin = new User();
        currentAdmin.setId(10L);

        Role role = mock(Role.class);
        currentAdmin.setRole(role);

        User assignedAdmin = new User();
        assignedAdmin.setId(20L);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketCode("TICKET-00001");
        ticket.setAssignedAdminAgent(assignedAdmin);

        when(ticketRepository.findTicketByTicketCode("TICKET-00001"))
                .thenReturn(Optional.of(ticket));

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(currentAdmin);

        when(role.isAdmin())
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> messageServiceImpl.sendAdminMessage(request)
        );

        verifyNoInteractions(modelMapper);
        verifyNoInteractions(messageRepository);
    }

    @Test
    void getMessagesByTicket_shouldReturnMessages_whenTicketExists() {

        String ticketCode = "TICKET-00001";

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketCode(ticketCode);

        User sender = new User();
        sender.setId(1L);
        sender.setFirstName("Akila");
        sender.setLastName("Customer");

        Role role = mock(Role.class);
        sender.setRole(role);

        when(role.getRoleName())
                .thenReturn("CUSTOMER");

        Message message1 = new Message();
        message1.setId(1L);
        message1.setTicket(ticket);
        message1.setSender(sender);

        Message message2 = new Message();
        message2.setId(2L);
        message2.setTicket(ticket);
        message2.setSender(sender);

        MessageResponse response1 = new MessageResponse();
        MessageResponse response2 = new MessageResponse();

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.of(ticket));

        when(messageRepository.findByTicketTicketCode(ticketCode))
                .thenReturn(List.of(message1, message2));

        when(modelMapper.map(message1, MessageResponse.class))
                .thenReturn(response1);

        when(modelMapper.map(message2, MessageResponse.class))
                .thenReturn(response2);

        List<MessageResponse> result =
                messageServiceImpl.getMessagesByTicket(ticketCode);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(ticketRepository)
                .findTicketByTicketCode(ticketCode);

        verify(messageRepository)
                .findByTicketTicketCode(ticketCode);

        verify(modelMapper)
                .map(message1, MessageResponse.class);

        verify(modelMapper)
                .map(message2, MessageResponse.class);
    }

    @Test
    void getMessagesByTicket_shouldThrowException_whenTicketNotFound() {

        String ticketCode = "TICKET-99999";

        when(ticketRepository.findTicketByTicketCode(ticketCode))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> messageServiceImpl.getMessagesByTicket(ticketCode)
        );

        verify(ticketRepository)
                .findTicketByTicketCode(ticketCode);

        verifyNoInteractions(messageRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void updateCustomerMessage_shouldUpdateMessage_whenCustomerOwnsMessage() {

        Long messageId = 1L;

        User customer = new User();
        customer.setId(1L);
        customer.setFirstName("Akila");
        customer.setLastName("Customer");

        Role customerRole = mock(Role.class);
        customer.setRole(customerRole);

        when(customerRole.getRoleName())
                .thenReturn("CUSTOMER");


        User sender = new User();
        sender.setId(1L);
        sender.setFirstName("Akila");
        sender.setLastName("Customer");
        sender.setRole(customerRole);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketCode("TICKET-00001");

        Message message = new Message();
        message.setId(messageId);
        message.setCreatedAt(
                LocalDateTime.now().minusMinutes(2)
        );
        message.setSender(sender);
        message.setTicket(ticket);
        message.setMessage("Old message");

        MessageUpdateRequest request = new MessageUpdateRequest();
        request.setMessage("Updated message");

        MessageResponse response = new MessageResponse();

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(customer);

        when(messageRepository.findById(messageId))
                .thenReturn(Optional.of(message));

        when(messageRepository.save(message))
                .thenReturn(message);

        when(modelMapper.map(message, MessageResponse.class))
                .thenReturn(response);

        MessageResponse result =
                messageServiceImpl.updateCustomerMessage(
                        messageId,
                        request
                );

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals("Updated message", message.getMessage());

        verify(messageRepository)
                .findById(messageId);

        verify(messageRepository)
                .save(message);

        verify(modelMapper)
                .map(message, MessageResponse.class);
    }


    @Test
    void updateAdminMessage_shouldUpdateMessage_whenAdminOwnsMessage() {

        Long messageId = 1L;

        User admin = new User();
        admin.setId(10L);
        admin.setFirstName("Pasan");
        admin.setLastName("Admin");

        Role adminRole = mock(Role.class);
        admin.setRole(adminRole);

        when(adminRole.getRoleName()).thenReturn("ADMIN");

        User sender = new User();
        sender.setId(10L);
        sender.setFirstName("Pasan");
        sender.setLastName("Admin");
        sender.setRole(adminRole);

        Ticket ticket = new Ticket();
        ticket.setId(10L);
        ticket.setTicketCode("TICKET-00001");
        ticket.setAssignedAdminAgent(admin);

        Message message = new Message();
        message.setId(messageId);
        message.setCreatedAt(LocalDateTime.now().minusMinutes(2));
        message.setSender(sender);
        message.setTicket(ticket);
        message.setMessage("The old message");


        MessageUpdateRequest request = new MessageUpdateRequest();
        request.setMessage("Updated admin message");

        MessageResponse response = new MessageResponse();


        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(admin);

        when(messageRepository.findById(messageId))
                .thenReturn(Optional.of(message));

        when(messageRepository.save(message))
                .thenReturn(message);

        when(modelMapper.map(message, MessageResponse.class))
                .thenReturn(response);

        MessageResponse result =
                messageServiceImpl.updateAdminMessage(messageId, request);

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals("Updated admin message", message.getMessage());

        verify(messageRepository)
                .findById(messageId);

        verify(messageRepository)
                .save(message);

        verify(modelMapper)
                .map(message, MessageResponse.class);
    }


    @Test
    void updateCustomerMessage_shouldThrowException_whenMessageDoesNotBelongToCustomer() {

        Long messageId = 1L;

        User customer = new User();
        customer.setId(1L);

        User sender = new User();
        sender.setId(2L);

        Message message = new Message();
        message.setId(messageId);
        message.setSender(sender);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(customer);

        when(messageRepository.findById(messageId))
                .thenReturn(Optional.of(message));

        assertThrows(
                BadRequestException.class,
                () -> messageServiceImpl.updateCustomerMessage(
                        messageId,
                        new MessageUpdateRequest()
                )
        );

        verify(messageRepository)
                .findById(messageId);

        verify(messageRepository, never())
                .save(any());

        verifyNoInteractions(modelMapper);
    }


    @Test
    void updateAdminMessage_shouldThrowException_whenMessageDoesNotBelongToAdmin() {

        Long messageId = 1L;

        User admin = new User();
        admin.setId(10L);

        User sender = new User();
        sender.setId(20L);

        Message message = new Message();
        message.setId(messageId);
        message.setSender(sender);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(admin);

        when(messageRepository.findById(messageId))
                .thenReturn(Optional.of(message));

        assertThrows(
                BadRequestException.class,
                () -> messageServiceImpl.updateAdminMessage(
                        messageId,
                        new MessageUpdateRequest()
                )
        );

        verify(messageRepository)
                .findById(messageId);

        verify(messageRepository, never())
                .save(any());

        verifyNoInteractions(modelMapper);
    }


    @Test
    void updateCustomerMessage_shouldThrowException_whenMessageOlderThanFiveMinutes() {

        Long messageId = 1L;

        User customer = new User();
        customer.setId(1L);

        User sender = new User();
        sender.setId(1L);

        Message message = new Message();
        message.setId(messageId);
        message.setSender(sender);

        message.setCreatedAt(
                LocalDateTime.now().minusMinutes(6)
        );

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(customer);

        when(messageRepository.findById(messageId))
                .thenReturn(Optional.of(message));

        assertThrows(
                BadRequestException.class,
                () -> messageServiceImpl.updateCustomerMessage(
                        messageId,
                        new MessageUpdateRequest()
                )
        );

        verify(messageRepository)
                .findById(messageId);

        verify(messageRepository, never())
                .save(any());

        verifyNoInteractions(modelMapper);
    }


    @Test
    void updateCustomerMessage_shouldThrowException_whenMessageNotFound() {

        Long messageId = 999L;

        User customer = new User();
        customer.setId(1L);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(customer);

        when(messageRepository.findById(messageId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> messageServiceImpl.updateCustomerMessage(
                        messageId,
                        new MessageUpdateRequest()
                )
        );

        verify(messageRepository)
                .findById(messageId);

        verify(messageRepository, never())
                .save(any());

        verifyNoInteractions(modelMapper);
    }
}