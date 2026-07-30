package com.susanthika.TicketProject.service.impl;

import com.susanthika.TicketProject.dto.request.TicketAdminUpdateRequest;
import com.susanthika.TicketProject.dto.request.TicketCustomerUpdateRequest;
import com.susanthika.TicketProject.dto.request.TicketRequest;
import com.susanthika.TicketProject.dto.response.TicketResponse;
import com.susanthika.TicketProject.entity.Department;
import com.susanthika.TicketProject.entity.Ticket;
import com.susanthika.TicketProject.entity.User;
import com.susanthika.TicketProject.entity.enums.Status;
import com.susanthika.TicketProject.exception.ResourceNotFoundException;
import com.susanthika.TicketProject.repository.DepartmentRepository;
import com.susanthika.TicketProject.repository.TicketRepository;
import com.susanthika.TicketProject.repository.UserRepository;
import com.susanthika.TicketProject.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private static final Long DEFAULT_CUSTOMER_ID = 4L;
    private static final Long DEFAULT_ADMIN_ID = 6L;
    private String adminUserName;


    @Override
    public TicketResponse createTicket(TicketRequest ticketRequest) {

        Department department = departmentRepository.findById(ticketRequest.getDepartmentId())
                .orElseThrow(()-> new ResourceNotFoundException("Department not found "));
//        User customer = userRepository.findByEmail(mail)
//                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
//
//        if (!"CUSTOMER".equals(customer.getRole().getRoleName())){
//            throw new IllegalArgumentException("Only customers can create tickets.");
//        }

        User customer = userRepository.findById(DEFAULT_CUSTOMER_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Default customer not found"));

        if (!"CUSTOMER".equalsIgnoreCase(customer.getRole().getRoleName())) {
            throw new IllegalArgumentException("Default user (Only customers can create tickets)");
        }

        Ticket ticket = modelMapper.map(ticketRequest, Ticket.class);

        ticket.setCustomer(customer);
        ticket.setDepartment(department);
        ticket.setStatus(Status.NEW);

        Ticket savedTicket = ticketRepository.save(ticket);
        savedTicket.setTicketCode(generateTicketCode(savedTicket.getId()));
        savedTicket = ticketRepository.save(savedTicket);
        return mapToResponse(savedTicket);

    }


    @Override
    public TicketResponse findTicketByTicketCode(String ticketCode) {
        Ticket ticket = ticketRepository.findTicketByTicketCode(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketCode));
        return mapToResponse(ticket);
    }



    @Override
    public TicketResponse updateTicketByCustomer(String ticketCode, TicketCustomerUpdateRequest request) { // updateTicketByCustomer

        Ticket ticket = ticketRepository.findTicketByTicketCode(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketCode));

        if (ticket.getAssignedAdminAgent() != null) {
            throw new IllegalArgumentException("Ticket cannot be updated after admin assignment");
        }

        //Ticket ticket1 = modelMapper.map(request, Ticket.class);

        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + request.getDepartmentId()));

        ticket.setDepartment(department);
        Ticket updatedTicket = ticketRepository.save(ticket);
        //Ticket updatedTicket = ticketRepository.save(ticket1);
        return mapToResponse(updatedTicket);
    }



    @Override
    public TicketResponse updateTicketStatusByAdmin(String ticketCode, TicketAdminUpdateRequest request) {

        Ticket ticket = ticketRepository.findTicketByTicketCode(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketCode));

        User admin = userRepository.findById(DEFAULT_ADMIN_ID)
                .orElseThrow(()-> new ResourceNotFoundException("Default admin not found"));

        if (!"ADMIN".equalsIgnoreCase(admin.getRole().getRoleName())){
            throw new IllegalArgumentException("Default user is not admin");
        }

        // Ticket ticket1 = modelMapper.map(request, Ticket.class);

        ticket.setAssignedAdminAgent(admin);
        ticket.setStatus(request.getStatus());
        Ticket updatedTicket = ticketRepository.save(ticket);
        return mapToResponse(updatedTicket);
    }



    @Override
    public void deleteTicketByTicketCode(String ticketCode) {

        Ticket ticket = ticketRepository.findTicketByTicketCode(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketCode));

        if (ticket.getAssignedAdminAgent() != null) {
            throw new IllegalArgumentException("The assigned tickets cannot be deleted");
        }
        ticketRepository.delete(ticket);
    }

    @Override
    public List<TicketResponse> findCustomerTickets() {
        User customer = userRepository.findById(DEFAULT_CUSTOMER_ID)
                .orElseThrow(()-> new ResourceNotFoundException("Customer not found"));
        return ticketRepository.findByCustomerId(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<TicketResponse> findAdminTickets() {
        User admin = userRepository.findById(DEFAULT_ADMIN_ID)
                .orElseThrow(()-> new ResourceNotFoundException("Admin not found"));

        if (!"ADMIN".equalsIgnoreCase(admin.getRole().getRoleName())){
            throw new IllegalArgumentException("User is not an admin");
        }

        return ticketRepository.findByAssignedAdminAgentId(admin.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public List<TicketResponse> findAllTicketsByManager() {
        return ticketRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    private String generateTicketCode(Long id) {
        return "TICKET-" + String.format("%05d", id);
    }

    private TicketResponse mapToResponse(Ticket ticket) {

        TicketResponse response = modelMapper.map(ticket, TicketResponse.class);
        if (ticket.getCustomer() != null) {
            response.setCustomerName(ticket.getCustomer().getFirstName() + " " + ticket.getCustomer().getLastName());
        }

        if (ticket.getDepartment() != null) {
            response.setDepartmentName(ticket.getDepartment().getDepartmentName());
        }

        if (ticket.getAssignedAdminAgent() != null) {
            response.setAssignedAdminAgentName(ticket.getAssignedAdminAgent().getFirstName() + " " + ticket.getAssignedAdminAgent().getLastName());
        }

        return response;
    }

}
