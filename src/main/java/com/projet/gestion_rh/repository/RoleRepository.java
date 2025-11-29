package com.projet.gestion_rh.repository;

import com.projet.gestion_rh.model.utils.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    // Optionnel : méthode pratique pour chercher par nom
    Role findByNomRole(String nomRole);
}

