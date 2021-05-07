package com.raulmello.crud.controller;

import com.raulmello.crud.model.Employee;
import com.raulmello.crud.repository.EmployeeRepository;
import com.raulmello.crud.services.EmployeeServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {

    private final EmployeeServices employeeServices;

    EmployeeController(EmployeeRepository employeeRepository, EmployeeServices employeeServices) {
        this.employeeServices = employeeServices;
    }

    @GetMapping("/employees")
    public List<Employee> findAll() {
        return employeeServices.findAll();
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity findById(@PathVariable long id) {
        return employeeServices.findById(id);
    }

    @PostMapping("/employee")
    public Employee create(@RequestBody Employee employee) {
        return employeeServices.create(employee);
    }

    @PutMapping("/employee/{id}")
    public ResponseEntity update(@PathVariable("id") long id, @RequestBody Employee employee) {
        return employeeServices.update(id, employee);
    }

    @DeleteMapping("/employee/{id}")
    public ResponseEntity delete(@PathVariable long id) {
        return employeeServices.delete(id);
    }
}
