package com.susanthika.TicketProject.repository;

import com.susanthika.TicketProject.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Long id(Long id);
}
