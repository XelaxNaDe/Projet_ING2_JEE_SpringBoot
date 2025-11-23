package com.projet.gestion_rh.repository;

import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll, Integer> {
    

    // "SELECT * FROM Payroll WHERE employee_id = ?"
    List<Payroll> findByEmployee(Employee employee);
    
}