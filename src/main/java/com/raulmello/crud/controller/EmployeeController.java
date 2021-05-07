package com.raulmello.crud.controller;

import com.raulmello.crud.model.Employee;
import com.raulmello.crud.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {

    private final EmployeeRepository repository;

    EmployeeController(EmployeeRepository employeeRepository) {
        this.repository = employeeRepository;
    }

    @GetMapping("/employees")
    public List<Employee> findAll() {
        return repository.findAll();
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity findById(@PathVariable long id) {
        return repository.findById(id).map(record -> ResponseEntity.ok().body(record)).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/employee")
    public Employee create(@RequestBody Employee employee) {
        return repository.save(employee);
    }

    @PutMapping("/employee/{id}")
    public ResponseEntity update(@PathVariable("id") long id, @RequestBody Employee employee) {
        return repository.findById(id).map(record -> {
            record.setFirstName(employee.getFirstName());
            record.setLastName(employee.getLastName());
            record.setInscriptionId(employee.getInscriptionId());
            record.setPosition(employee.getPosition());
            record.setBranch(employee.getBranch());
            record.setManager(employee.getManager());
            record.setIncome(employee.getIncome());

            final Employee updated = repository.save(record);
            return ResponseEntity.ok().body(updated);

        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/employee/{id}")
    public ResponseEntity delete(@PathVariable long id) {
        return repository.findById(id).map(record -> {
            repository.deleteById(id);

            return ResponseEntity.ok().body(record);
        }).orElse(ResponseEntity.notFound().build());
    }
}
