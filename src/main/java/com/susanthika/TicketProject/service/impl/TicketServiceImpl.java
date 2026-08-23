package com.susanthika.TicketProject.service.impl;

import com.susanthika.TicketProject.dto.request.TicketAdminUpdateRequest;
import com.susanthika.TicketProject.dto.request.TicketAssignRequest;
import com.susanthika.TicketProject.dto.request.TicketCustomerUpdateRequest;
import com.susanthika.TicketProject.dto.request.TicketRequest;
import com.susanthika.TicketProject.dto.response.TicketResponse;
import com.susanthika.TicketProject.entity.Department;
import com.susanthika.TicketProject.entity.Ticket;
import com.susanthika.TicketProject.entity.User;
import com.susanthika.TicketProject.entity.enums.Status;
import com.susanthika.TicketProject.exception.BadRequestException;
import com.susanthika.TicketProject.exception.ResourceNotFoundException;
import com.susanthika.TicketProject.repository.DepartmentRepository;
import com.susanthika.TicketProject.repository.TicketRepository;
import com.susanthika.TicketProject.repository.UserRepository;
import com.susanthika.TicketProject.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public TicketResponse createTicket(TicketRequest ticketRequest) {

        User customer = getCurrentUser();

        if (!customer.getRole().isCustomer()) {
            throw new BadRequestException("Only customers can create tickets");
        }

        Department department = departmentRepository.findById(ticketRequest.getDepartmentId())
                .orElseThrow(()-> new ResourceNotFoundException("Department not found "));

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
    public TicketResponse assignTicketToAdmin(String ticketCode, TicketAssignRequest ticketAssignRequest) {
        Ticket ticket = ticketRepository.findTicketByTicketCode(ticketCode)
                .orElseThrow(()-> new ResourceNotFoundException("Ticket not found: "+ ticketCode));

        if (ticket.getAssignedAdminAgent() != null){
            throw new BadRequestException("This ticket is already assigned to another admin");
        }

        User admin = userRepository.findById(ticketAssignRequest.getAdminId())
                .orElseThrow(()-> new ResourceNotFoundException("Admin not found: " + ticketAssignRequest.getAdminId()));

        if (!admin.getRole().isAdmin()) {
            throw new BadRequestException("User is not an admin");
        }

        if (!ticket.getDepartment().getId().equals(admin.getDepartment().getId())){
            throw new BadRequestException("Admin cannot take tickets from another department");
        }

        ticket.setAssignedAdminAgent(admin);
        ticket.setStatus(Status.IN_PROGRESS);

        Ticket savedTicket = ticketRepository.save(ticket);

        return mapToResponse(savedTicket);
    }


    @Override
    public TicketResponse updateTicketByCustomer(String ticketCode, TicketCustomerUpdateRequest request) {

        Ticket ticket = ticketRepository.findTicketByTicketCode(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketCode));

        User customer = getCurrentUser();

        if (!customer.getRole().isCustomer()) {
            throw new BadRequestException("Only customers can update tickets");
        }

        if (!ticket.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException( "You can only update your own tickets" );
        }

        if (ticket.getAssignedAdminAgent() != null) {
            throw new BadRequestException("Ticket cannot be updated after admin assignment");
        }

        if (request.getTitle() != null) {
            ticket.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }

        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository .findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException( "Department not found: " + request.getDepartmentId() ));
            ticket.setDepartment(department); }

        Ticket updatedTicket = ticketRepository.save(ticket);
        return mapToResponse(updatedTicket);
    }


    @Override
    public TicketResponse updateTicketStatusByAdmin(String ticketCode, TicketAdminUpdateRequest request) {

        Ticket ticket = ticketRepository.findTicketByTicketCode(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketCode));

        User admin = getCurrentUser();

        if (!admin.getRole().isAdmin()) {
            throw new BadRequestException("Only admins can update ticket status");
        }

        if (ticket.getAssignedAdminAgent() == null) {
            throw new BadRequestException( "Ticket has not been assigned to an admin" );
        }

        if (!ticket.getAssignedAdminAgent().getId()
                .equals(admin.getId())) {
            throw new BadRequestException( "You can only update tickets assigned to you" );
        }

        ticket.setStatus(request.getStatus());
        Ticket updatedTicket = ticketRepository.save(ticket);
        return mapToResponse(updatedTicket);

    }


    @Override
    public void deleteTicketByTicketCode(String ticketCode) {

        Ticket ticket = ticketRepository.findTicketByTicketCode(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketCode));

        User customer = getCurrentUser();

        if (!customer.getRole().isCustomer()) {
            throw new BadRequestException("Only customers can delete tickets");
        }

        if (!ticket.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException( "You can only delete your own tickets" );
        }

        if (ticket.getAssignedAdminAgent() != null) {
            throw new BadRequestException( "Assigned tickets cannot be deleted" );
        }

        ticketRepository.delete(ticket);
    }

    @Override
    public List<TicketResponse> findCustomerTickets() {

        User customer = getCurrentUser();

        if (!customer.getRole().isCustomer()) {
            throw new BadRequestException("Only customers can view customer tickets");
        }


        return ticketRepository
                .findByCustomerId(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<TicketResponse> findAllAdminTickets() {

        return ticketRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public List<TicketResponse> findTicketsByAdminId(Long adminId) {

        User manager = getCurrentUser();

        if (!manager.getRole().isManager()) {
            throw new BadRequestException("Only managers can view tickets by admin");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException( "Admin not found: " + adminId ));

        if (!admin.getRole().isAdmin()) {
            throw new BadRequestException("User is not an admin");
        }

        return ticketRepository
                .findByAssignedAdminAgentId(adminId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public List<TicketResponse> findAllTicketsByManager() {

        User manager = getCurrentUser();

        if (!manager.getRole().isManager()) {
            throw new BadRequestException("Only managers can view all tickets");
        }

        return ticketRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException( "Authenticated user not found" ));
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
