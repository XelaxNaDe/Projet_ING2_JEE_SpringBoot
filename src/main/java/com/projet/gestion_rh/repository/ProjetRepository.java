package com.projet.gestion_rh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.projet.gestion_rh.model.Projet;

public interface ProjetRepository extends JpaRepository<Projet, Integer> {
        @Query("SELECT p.nomProjet AS label, COUNT(e) AS valeur " +
               "FROM Projet p JOIN p.equipe e GROUP BY p.nomProjet")
        List<Stat> countEmployeesByProjet();

        @Query("SELECT p.etat AS label, COUNT(p) AS valeur " +
               "FROM Projet p GROUP BY p.etat")
        List<Stat> countProjetsByEtat();

    List<Projet> findByChefProjetId(int idChef);
}
