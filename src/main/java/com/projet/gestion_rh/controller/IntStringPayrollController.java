package com.projet.gestion_rh.controller;

import com.projet.gestion_rh.model.utils.IntStringPayroll;
import com.projet.gestion_rh.repository.IntStringPayrollRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll-lines")
public class IntStringPayrollController {

    private final IntStringPayrollRepository lineRepository;

    public IntStringPayrollController(IntStringPayrollRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @GetMapping
    public List<IntStringPayroll> getAll() {
        return lineRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IntStringPayroll> getOne(@PathVariable int id) {
        return lineRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public IntStringPayroll create(@RequestBody IntStringPayroll line) {
        return lineRepository.save(line);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IntStringPayroll> update(@PathVariable int id,
                                                   @RequestBody IntStringPayroll updatedLine) {
        return lineRepository.findById(id)
                .map(existing -> {
                    existing.setAmount(updatedLine.getAmount());
                    existing.setLabel(updatedLine.getLabel());
                    existing.setTypeList(updatedLine.getTypeList());
                    existing.setPayroll(updatedLine.getPayroll());
                    return ResponseEntity.ok(lineRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (!lineRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        lineRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
