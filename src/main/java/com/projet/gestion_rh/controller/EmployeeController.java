package com.projet.gestion_rh.controller;

import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.repository.DepartementRepository;
import com.projet.gestion_rh.repository.EmployeeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final DepartementRepository departementRepository;

    public EmployeeController(EmployeeRepository employeeRepository, DepartementRepository departementRepository) {
        this.employeeRepository = employeeRepository;
        this.departementRepository = departementRepository;
    }

    // LECTURE : Tout le monde connecté peut voir la liste
    @GetMapping("/employees")
    public String listEmployees(Model model, HttpSession session) {
        if (session.getAttribute("currentUser") == null) return "redirect:/login";

        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("departements", departementRepository.findAll());
        return "employees"; 
    }

    // AJOUT : Seul l'ADMIN peut ajouter
    @PostMapping("/employees/add")
    public String addEmployee(@RequestParam String fname,
                              @RequestParam String sname,
                              @RequestParam(required = false) String gender,
                              @RequestParam String email,
                              @RequestParam(required = false) String position,
                              @RequestParam(required = false) String grade,
                              @RequestParam(required = false) Integer departementId,
                              HttpSession session) {
        
        Employee user = (Employee) session.getAttribute("currentUser");
        
        if (user != null && user.hasRole("ADMINISTRATOR")) {
            Employee e = new Employee();
            e.setFname(fname); e.setSname(sname); e.setGender(gender);
            e.setEmail(email); e.setPassword("password"); // Défaut
            e.setPosition(position); e.setGrade(grade);

            if (departementId != null) {
                departementRepository.findById(departementId).ifPresent(e::setDepartement);
            }
            employeeRepository.save(e);
        }
        return "redirect:/employees";
    }

    // SUPPRESSION : Seul l'ADMIN peut supprimer
    @PostMapping("/employees/delete")
    public String deleteEmployee(@RequestParam int id, HttpSession session) {
        Employee user = (Employee) session.getAttribute("currentUser");

        if (user != null && user.hasRole("ADMINISTRATOR")) {
            if (employeeRepository.existsById(id)) {
                employeeRepository.deleteById(id);
            }
        }
        return "redirect:/employees";
    }
}