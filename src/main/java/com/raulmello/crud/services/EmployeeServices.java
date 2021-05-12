package com.raulmello.crud.services;

import com.raulmello.crud.model.Employee;
import com.raulmello.crud.repository.EmployeeRepository;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

@Service
public class EmployeeServices {

    private final EmployeeRepository repository;

    EmployeeServices(EmployeeRepository employeeRepository) {
        this.repository = employeeRepository;
    }

    public List<Employee> findAll() {
        return repository.findAll();
    }

    public ResponseEntity findById(long id) {
        return repository.findById(id)
                .map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    public Employee create(Employee employee) {
        return repository.save(employee);
    }

    public ResponseEntity update(long id, Employee employee) {
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

    public ResponseEntity partiallyUpdate(long id, HashMap map) {
        return repository.findById(id).map(record -> {
            try {
                BeanUtils.populate(record, map);

                final Employee updated = repository.save(record);
                return ResponseEntity.ok().body(updated);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }

        }).orElse(ResponseEntity.notFound().build());

    }

    public ResponseEntity delete(long id) {
        return repository.findById(id).map(record -> {
            repository.deleteById(id);

            return ResponseEntity.ok().body(record);

        }).orElse(ResponseEntity.notFound().build());
    }
}
