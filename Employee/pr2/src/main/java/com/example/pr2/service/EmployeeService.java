package com.example.pr2.service;

import com.example.pr2.model.Employee;
import com.example.pr2.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository repository;

    public List<Employee> search(String name, Boolean active) {
        boolean hasName = name != null && !name.isBlank();
        String q = hasName ? name.trim() : null;

        if (hasName && active != null) {
            return repository.findByFullNameContainingIgnoreCaseAndActive(q, active);
        }
        if (hasName) {
            return repository.findByFullNameContainingIgnoreCase(q);
        }
        if (active != null) {
            return repository.findByActive(active);
        }
        return repository.findAll();
    }

    public Employee getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Сотрудник с id=" + id + " не найден"));
    }

    public boolean emailExists(String email) {
        return repository.existsByEmail(email);
    }

    /** Проверка email при редактировании: свой собственный email не считается дубликатом. */
    public boolean emailExists(String email, Long excludeId) {
        return excludeId == null
                ? repository.existsByEmail(email)
                : repository.existsByEmailAndIdNot(email, excludeId);
    }

    public Employee save(Employee employee) {
        return repository.save(employee);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}