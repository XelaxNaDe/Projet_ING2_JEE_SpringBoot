package com.projet.gestion_rh.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.projet.gestion_rh.model.Departement;
import com.projet.gestion_rh.model.Employee;

// <Employee, Integer> car l'ID de Employee est un 'int'
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    
    // Pour le Login
    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByEmailAndPassword(String email, String password);

    List<Employee> findByDepartement(Departement departement);

    // Recherche : prénom, nom, poste, departement
    @Query("""
           SELECT e FROM Employee e
           WHERE (:fname IS NULL OR LOWER(e.fname) LIKE LOWER(CONCAT('%', :fname, '%')))
             AND (:sname IS NULL OR LOWER(e.sname) LIKE LOWER(CONCAT('%', :sname, '%')))
             AND (:position IS NULL OR LOWER(e.position) LIKE LOWER(CONCAT('%', :position, '%')))
             AND (:departementId IS NULL OR (e.departement IS NOT NULL AND e.departement.idDepartement = :departementId))
           """)
    List<Employee> search(
            @Param("fname") String fname,
            @Param("sname") String sname,
            @Param("position") String position,
            @Param("departementId") Integer departementId
    );

}
