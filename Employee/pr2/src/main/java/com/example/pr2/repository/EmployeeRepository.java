package com.example.pr2.repository;

import com.example.pr2.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    List<Employee> findByFullNameContainingIgnoreCase(String name);
    List<Employee> findByActive(boolean active);
    List<Employee> findByFullNameContainingIgnoreCaseAndActive(String name, boolean active);
}
