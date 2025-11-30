package com.projet.gestion_rh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.repository.DepartementRepository;
import com.projet.gestion_rh.repository.EmployeeRepository;
import com.projet.gestion_rh.repository.ProjetRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class StatsController {

    private final EmployeeRepository employeeRepository;
    private final DepartementRepository departementRepository;
    private final ProjetRepository projetRepository;

    public StatsController(EmployeeRepository er, DepartementRepository dr, ProjetRepository pr) {
        this.employeeRepository = er;
        this.departementRepository = dr;
        this.projetRepository = pr;
    }

    @GetMapping("/statistics")
    public String showStatistics(Model model, HttpSession session) {
        Employee user = (Employee) session.getAttribute("currentUser");
        
        // y'a que l'admin ou le Chef de Dept peut voir les stats
        if (user == null || (!user.hasRole("ADMINISTRATOR") && !user.hasRole("HEADDEPARTEMENT"))) {
            return "redirect:/";
        }

        model.addAttribute("empByDept", departementRepository.countEmployeesByDepartement());

        model.addAttribute("empByProjet", projetRepository.countEmployeesByProjet());

        model.addAttribute("projetByEtat", projetRepository.countProjetsByEtat());

        return "statistics";
    }
}
