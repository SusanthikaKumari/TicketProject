package com.susanthika.TicketProject.controller;

import com.susanthika.TicketProject.dto.request.TicketAdminUpdateRequest;
import com.susanthika.TicketProject.dto.request.TicketCustomerUpdateRequest;
import com.susanthika.TicketProject.dto.request.TicketRequest;
import com.susanthika.TicketProject.dto.response.TicketResponse;
import com.susanthika.TicketProject.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/{ticketCode}")
    @ResponseStatus(HttpStatus.OK)
    public TicketResponse getTicketByTicketCode(@PathVariable String ticketCode){
        return ticketService.findTicketByTicketCode(ticketCode);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(@Valid @RequestBody TicketRequest ticketRequest){
        return ticketService.createTicket(ticketRequest);
    }

    @PutMapping("/{ticketCode}")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse updateTicket(
            @PathVariable String ticketCode,
            @Valid @RequestBody TicketCustomerUpdateRequest ticketCustomerUpdateRequest) {

        return ticketService.updateTicketByCustomer(ticketCode, ticketCustomerUpdateRequest);
    }

    @PatchMapping("/{ticketCode}")
    @ResponseStatus(HttpStatus.OK)
    public TicketResponse updateTicketStatus(
            @PathVariable String ticketCode,
            @Valid @RequestBody TicketAdminUpdateRequest ticketAdminUpdateRequest) {

        return ticketService.updateTicketStatusByAdmin(ticketCode, ticketAdminUpdateRequest);
    }

    @DeleteMapping("/{ticketCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicket(@PathVariable String ticketCode){
        ticketService.deleteTicketByTicketCode(ticketCode);
    }

    @GetMapping("/customer")
    @ResponseStatus(HttpStatus.OK)
    public List<TicketResponse> getCustomerTickets(){
        return ticketService.findCustomerTickets();
    }

    @GetMapping("/admin")
    @ResponseStatus(HttpStatus.OK)
    public List<TicketResponse> getAdminTickets(){
        return ticketService.findAdminTickets();
    }

    @GetMapping("/manager")
    @ResponseStatus(HttpStatus.OK)
    public List<TicketResponse> getAllTickets(){
        return ticketService.findAllTicketsByManager();
    }
}
