package com.projet.gestion_rh.repository;

import com.projet.gestion_rh.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Integer> {
    // Récupérer toutes les fiches de paie d'un employé
    List<Payroll> findByEmployeeId(int employeeId);
}