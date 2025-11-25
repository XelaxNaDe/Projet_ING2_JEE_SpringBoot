package com.projet.gestion_rh.repository;

import com.projet.gestion_rh.model.Departement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartementRepository extends JpaRepository<Departement, Integer> {
    
}