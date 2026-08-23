package com.susanthika.TicketProject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true, length = 100)
    private String roleName;

    public boolean isCustomer() {
        return "CUSTOMER".equalsIgnoreCase(roleName);
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(roleName);
    }

    public boolean isManager() {
        return "MANAGER".equalsIgnoreCase(roleName);
    }
}
