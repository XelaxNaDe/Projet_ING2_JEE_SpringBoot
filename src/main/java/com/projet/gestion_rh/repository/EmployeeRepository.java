package com.projet.gestion_rh.repository;

import com.projet.gestion_rh.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// <Employee, Integer> car l'ID de Employee est un 'int'
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // Pour le Login : Spring génère "SELECT * FROM Employee WHERE email = ?"
    // On utilise Optional pour éviter les NullPointerException si l'email n'existe pas
    Optional<Employee> findByEmail(String email);

    // Pour vérifier le mot de passe (si vous ne le hashez pas encore)
    Optional<Employee> findByEmailAndPassword(String email, String password);
    
    // Pour la recherche par département (si besoin)
    // Spring comprend que "Departement" est un objet et cherche par son ID
    // List<Employee> findByDepartementId(int departementId);
}