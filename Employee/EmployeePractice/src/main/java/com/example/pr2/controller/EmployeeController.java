package com.example.pr2.controller;

import com.example.pr2.model.Employee;
import com.example.pr2.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    @GetMapping
    public String list(@RequestParam(required = false) String name,
                       @RequestParam(defaultValue = "all") String status,
                       Model model) {
        Boolean active = switch (status) {
            case "active" -> Boolean.TRUE;
            case "inactive" -> Boolean.FALSE;
            default -> null;
        };
        model.addAttribute("employees", service.search(name, active));
        model.addAttribute("name", name);
        model.addAttribute("status", status);
        return "employees-list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("title", "Добавление сотрудника");
        return "employee-form";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("employee") Employee employee,
                      BindingResult result, Model model) {
        if (!result.hasFieldErrors("email") && service.emailExists(employee.getEmail())) {
            result.rejectValue("email", "duplicate", "Сотрудник с таким email уже существует");
        }
        if (result.hasErrors()) {
            model.addAttribute("title", "Добавление сотрудника");
            return "employee-form";
        }
        service.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("employee", service.getById(id));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        model.addAttribute("title", "Редактирование сотрудника");
        return "employee-form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("employee") Employee employee,
                       BindingResult result, Model model) {
        employee.setId(id);
        if (!result.hasFieldErrors("email") && service.emailExists(employee.getEmail(), id)) {
            result.rejectValue("email", "duplicate", "Сотрудник с таким email уже существует");
        }
        if (result.hasErrors()) {
            model.addAttribute("title", "Редактирование сотрудника");
            return "employee-form";
        }
        service.save(employee);
        return "redirect:/employees";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/employees";
    }
}