package com.susanthika.TicketProject.controller;

import com.susanthika.TicketProject.dto.request.TicketAdminUpdateRequest;
import com.susanthika.TicketProject.dto.request.TicketAssignRequest;
import com.susanthika.TicketProject.dto.request.TicketCustomerUpdateRequest;
import com.susanthika.TicketProject.dto.request.TicketRequest;
import com.susanthika.TicketProject.dto.response.ApiResponse;
import com.susanthika.TicketProject.dto.response.TicketResponse;
import com.susanthika.TicketProject.service.TicketService;
import com.susanthika.TicketProject.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/{ticketCode}")
    public ResponseEntity<ApiResponse<TicketResponse>> findTicketByTicketCode(@PathVariable String ticketCode){
        TicketResponse response = ticketService.findTicketByTicketCode(ticketCode);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Ticket retrieved successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(@Valid @RequestBody TicketRequest ticketRequest){
        TicketResponse response = ticketService.createTicket(ticketRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponseUtil.success(
                                "Ticket created successfully",
                                response,
                                HttpStatus.CREATED
                        )
                );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{ticketCode}/assign")
    public ResponseEntity<ApiResponse<TicketResponse>> assignTicket(
            @PathVariable String ticketCode,
            @Valid @RequestBody TicketAssignRequest ticketAssignRequest){
        TicketResponse response = ticketService.assignTicketToAdmin(ticketCode, ticketAssignRequest);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Ticket assigned successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{ticketCode}")
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicket(
            @PathVariable String ticketCode,
            @Valid @RequestBody TicketCustomerUpdateRequest ticketCustomerUpdateRequest) {
        TicketResponse response = ticketService.updateTicketByCustomer(ticketCode, ticketCustomerUpdateRequest);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Ticket updated successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{ticketCode}")
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicketStatus(
            @PathVariable String ticketCode,
            @Valid @RequestBody TicketAdminUpdateRequest ticketAdminUpdateRequest) {

        TicketResponse response = ticketService.updateTicketStatusByAdmin(ticketCode, ticketAdminUpdateRequest);
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Ticket status updated successfully",
                        response
                )
        );
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @DeleteMapping("/{ticketCode}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable String ticketCode){
        ticketService.deleteTicketByTicketCode(ticketCode);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        "Ticket deleted successfully",
                        null
                )
        );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> findCustomerTickets(){
        List<TicketResponse> response = ticketService.findCustomerTickets();

        String message = response.isEmpty() ? "No tickets found" : "Customer tickets retrieved successfully";
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        message,
                        response
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> findAllAdminTickets(){
        List<TicketResponse> response = ticketService.findAllAdminTickets();

        String message = response.isEmpty() ? "No tickets found" : "Admin tickets retrieved successfully";

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        message,
                        response
                )
        );
    }


    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> findTicketsByAdminId(@PathVariable Long adminId){
        List<TicketResponse> response = ticketService.findTicketsByAdminId(adminId);

        String message = response.isEmpty() ? "No tickets found" : "Admin tickets retrieved successfully";

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        message,
                        response
                )
        );
    }


    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> findAllTickets(){
        List<TicketResponse> response = ticketService.findAllTicketsByManager();

        String message = response.isEmpty() ? "No tickets found" : "All tickets retrieved successfully";

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        message,
                        response
                )
        );
    }
}
