package com.projet.gestion_rh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.projet.gestion_rh.model.Departement;

public interface DepartementRepository extends JpaRepository<Departement, Integer> {
    @Query("SELECT d.nomDepartement AS label, COUNT(e) AS valeur " +
               "FROM Employee e JOIN e.departement d GROUP BY d.nomDepartement")
        List<Stat> countEmployeesByDepartement();
}