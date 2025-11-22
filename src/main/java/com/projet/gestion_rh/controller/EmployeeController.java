package com.projet.gestion_rh.controller;

import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getOne(@PathVariable int id) {
        return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Employee create(@RequestBody Employee employee) {
        // TODO plus tard : encoder password, vérifier email unique, etc.
        return employeeRepository.save(employee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable int id,
                                           @RequestBody Employee emp) {
        return employeeRepository.findById(id)
                .map(existing -> {
                    existing.setFname(emp.getFname());
                    existing.setSname(emp.getSname());
                    existing.setGender(emp.getGender());
                    existing.setEmail(emp.getEmail());
                    existing.setPassword(emp.getPassword());
                    existing.setPosition(emp.getPosition());
                    existing.setGrade(emp.getGrade());
                    existing.setDepartement(emp.getDepartement());
                    existing.setRoles(emp.getRoles());
                    existing.setProjets(emp.getProjets());
                    return ResponseEntity.ok(employeeRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (!employeeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        employeeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

