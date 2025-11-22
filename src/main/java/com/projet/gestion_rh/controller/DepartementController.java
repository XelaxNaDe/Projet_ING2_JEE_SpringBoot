package com.projet.gestion_rh.controller;

import com.projet.gestion_rh.model.Departement;
import com.projet.gestion_rh.repository.DepartementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departements")
public class DepartementController {

    private final DepartementRepository departementRepository;

    public DepartementController(DepartementRepository departementRepository) {
        this.departementRepository = departementRepository;
    }

    @GetMapping
    public List<Departement> getAll() {
        return departementRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departement> getOne(@PathVariable int id) {
        return departementRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Departement create(@RequestBody Departement dpt) {
        // id ignoré si fourni
        return departementRepository.save(dpt);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Departement> update(@PathVariable int id,
                                              @RequestBody Departement dpt) {
        return departementRepository.findById(id)
                .map(existing -> {
                    existing.setNomDepartement(dpt.getNomDepartement());
                    existing.setIdChefDepartement(dpt.getIdChefDepartement());
                    return ResponseEntity.ok(departementRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (!departementRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        departementRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
