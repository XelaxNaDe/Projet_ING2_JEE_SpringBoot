package com.projet.gestion_rh.controller;

import com.projet.gestion_rh.model.Projet;
import com.projet.gestion_rh.repository.ProjetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projets")
public class ProjetController {

    private final ProjetRepository projetRepository;

    public ProjetController(ProjetRepository projetRepository) {
        this.projetRepository = projetRepository;
    }

    @GetMapping
    public List<Projet> getAll() {
        return projetRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projet> getOne(@PathVariable int id) {
        return projetRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Projet create(@RequestBody Projet projet) {
        return projetRepository.save(projet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Projet> update(@PathVariable int id,
                                         @RequestBody Projet p) {
        return projetRepository.findById(id)
                .map(existing -> {
                    existing.setNomProjet(p.getNomProjet());
                    existing.setDateDebut(p.getDateDebut());
                    existing.setDateFin(p.getDateFin());
                    existing.setEtat(p.getEtat());
                    existing.setChefProjet(p.getChefProjet());
                    existing.setEquipe(p.getEquipe());
                    return ResponseEntity.ok(projetRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (!projetRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        projetRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
