package com.projet.gestion_rh.repository;

import com.projet.gestion_rh.model.utils.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByNomRole(String nomRole);
}

