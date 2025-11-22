package com.projet.gestion_rh.controller;

import com.projet.gestion_rh.model.Payroll;
import com.projet.gestion_rh.repository.PayrollRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payrolls")
public class PayrollController {

    private final PayrollRepository payrollRepository;

    public PayrollController(PayrollRepository payrollRepository) {
        this.payrollRepository = payrollRepository;
    }

    @GetMapping
    public List<Payroll> getAll() {
        return payrollRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payroll> getOne(@PathVariable int id) {
        return payrollRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Payroll create(@RequestBody Payroll payroll) {
        // TODO plus tard : calcul netPay, gestion des lignes, etc.
        return payrollRepository.save(payroll);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payroll> update(@PathVariable int id,
                                          @RequestBody Payroll p) {
        return payrollRepository.findById(id)
                .map(existing -> {
                    existing.setEmployee(p.getEmployee());
                    existing.setDate(p.getDate());
                    existing.setSalary(p.getSalary());
                    existing.setNetPay(p.getNetPay());
                    existing.setLines(p.getLines());
                    return ResponseEntity.ok(payrollRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (!payrollRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        payrollRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
