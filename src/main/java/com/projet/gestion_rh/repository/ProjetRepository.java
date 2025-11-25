package com.projet.gestion_rh.repository;

import com.projet.gestion_rh.model.Projet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjetRepository extends JpaRepository<Projet, Integer> {
    // Trouver les projets d'un chef spécifique
    List<Projet> findByChefProjetId(int idChef);
}