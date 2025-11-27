package com.projet.gestion_rh.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
    
import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.repository.DepartementRepository;
import com.projet.gestion_rh.repository.EmployeeRepository;

import jakarta.servlet.http.HttpSession;    

@Controller
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final DepartementRepository departementRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeController(EmployeeRepository employeeRepository, DepartementRepository departementRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.departementRepository = departementRepository;
        this.passwordEncoder = passwordEncoder;
        
    }

    @GetMapping("/employees")
    public String listEmployees(@RequestParam(required = false) String fname,
                                @RequestParam(required = false) String sname,
                                @RequestParam(required = false) String position,
                                @RequestParam(required = false) Integer departementId,
                                Model model,
                                HttpSession session) {
        if (session.getAttribute("currentUser") == null) return "redirect:/login";

        // Nettoyer les chaînes vides -> null (pour la requête JPQL)
        if (fname != null && fname.trim().isEmpty()) fname = null;
        if (sname != null && sname.trim().isEmpty()) sname = null;
        if (position != null && position.trim().isEmpty()) position = null;

        boolean hasFilter = (fname != null) || (sname != null) || (position != null) || (departementId != null);

        if (hasFilter) {
            model.addAttribute("employees",
                    employeeRepository.search(fname, sname, position, departementId));
        } else {
            model.addAttribute("employees", employeeRepository.findAll());
        }

        // Liste des départements (pour ajout + filtre)
        model.addAttribute("departements", departementRepository.findAll());

        // Pour re-remplir le formulaire de recherche
        model.addAttribute("fname", fname);
        model.addAttribute("sname", sname);
        model.addAttribute("position", position);
        model.addAttribute("departementId", departementId);

        return "employees";
    }


    @GetMapping("/employees/edit")
    public String editEmployeeForm(@RequestParam int id, Model model, HttpSession session) {
        if (session.getAttribute("currentUser") == null) return "redirect:/login";

        Employee user = (Employee) session.getAttribute("currentUser");
        if (user == null || !user.hasRole("ADMINISTRATOR")) {
            return "redirect:/employees";
        }

        Employee e = employeeRepository.findById(id).orElse(null);
        if (e == null) {
            return "redirect:/employees";
        }

        model.addAttribute("employee", e);
        model.addAttribute("departements", departementRepository.findAll());
        return "employee-edit"; // nouveau template
    }

    @PostMapping("/employees/update")
    public String updateEmployee(@RequestParam int id,
                                 @RequestParam String fname,
                                 @RequestParam String sname,
                                 @RequestParam(required = false) String gender,
                                 @RequestParam String email,
                                 @RequestParam(required = false) String position,
                                 @RequestParam(required = false) Integer departementId,
                                 HttpSession session) {

        Employee user = (Employee) session.getAttribute("currentUser");
        if (user == null || !user.hasRole("ADMINISTRATOR")) {
            return "redirect:/employees";
        }

        Employee e = employeeRepository.findById(id).orElse(null);
        if (e == null) {
            return "redirect:/employees";
        }

        e.setFname(fname);
        e.setSname(sname);
        e.setGender(gender);
        e.setEmail(email);
        e.setPosition(position);

        if (departementId != null) {
            departementRepository.findById(departementId).ifPresent(e::setDepartement);
        } else {
            e.setDepartement(null);
        }

        employeeRepository.save(e);
        return "redirect:/employees";
    }




    // AJOUT : Seul l'ADMIN peut ajouter
    @PostMapping("/employees/add")
    public String addEmployee(@RequestParam String fname,
                              @RequestParam String sname,
                              @RequestParam(required = false) String gender,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam(required = false) String position,
                              @RequestParam(required = false) Integer departementId,
                              HttpSession session) {
        
        Employee user = (Employee) session.getAttribute("currentUser");
        
        if (user != null && user.hasRole("ADMINISTRATOR")) {
            Employee e = new Employee();
            e.setFname(fname); e.setSname(sname); e.setGender(gender);
            e.setEmail(email);
            e.setPassword(passwordEncoder.encode(password));

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