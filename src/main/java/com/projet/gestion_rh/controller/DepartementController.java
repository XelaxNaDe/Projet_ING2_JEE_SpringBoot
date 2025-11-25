package com.projet.gestion_rh.controller;

import com.projet.gestion_rh.model.Departement;
import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.repository.DepartementRepository;
import com.projet.gestion_rh.repository.EmployeeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class DepartementController {

    private final DepartementRepository departementRepository;
    private final EmployeeRepository employeeRepository;

    public DepartementController(DepartementRepository departementRepository, EmployeeRepository employeeRepository) {
        this.departementRepository = departementRepository;
        this.employeeRepository = employeeRepository;
    }

    // Sécurité : Vérifie si c'est le chef du département ou un admin
    private boolean canEditDept(Departement d, Employee user) {
        if (user.hasRole("ADMINISTRATOR")) return true;
        if (user.hasRole("HEADDEPARTEMENT")) {
            // Attention: idChefDepartement est un Integer, on compare avec user.getId()
            return d.getIdChefDepartement() != null && d.getIdChefDepartement().equals(user.getId());
        }
        return false;
    }

    @GetMapping("/departements")
    public String departements(@RequestParam(name = "selectedDeptId", required = false) Integer selectedDeptId,
                               Model model, HttpSession session) {
        if (session.getAttribute("currentUser") == null) return "redirect:/login";

        model.addAttribute("departements", departementRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());

        if (selectedDeptId != null) {
            Optional<Departement> optDept = departementRepository.findById(selectedDeptId);
            if (optDept.isPresent()) {
                Departement d = optDept.get();
                List<Employee> members = employeeRepository.findByDepartement(d);
                model.addAttribute("selectedDepartement", d);
                model.addAttribute("members", members);
            }
        }
        return "departements";
    }

    // Ajouter : Seulement ADMIN
    @PostMapping("/departements/add")
    public String addDepartement(@RequestParam String nomDepartement,
                                 @RequestParam(required = false) Integer idChefDepartement,
                                 HttpSession session) {
        Employee user = (Employee) session.getAttribute("currentUser");
        if (user != null && user.hasRole("ADMINISTRATOR")) {
            Departement d = new Departement();
            d.setNomDepartement(nomDepartement);
            d.setIdChefDepartement(idChefDepartement);
            departementRepository.save(d);
        }
        return "redirect:/departements";
    }

    // Assigner employé : ADMIN ou CHEF DU DEPARTEMENT concerné
    @PostMapping("/departements/assign")
    public String assignEmployeeToDepartement(@RequestParam int deptId, 
                                              @RequestParam int empId,
                                              HttpSession session) {
        Employee user = (Employee) session.getAttribute("currentUser");
        Optional<Departement> optDept = departementRepository.findById(deptId);
        Optional<Employee> optEmp = employeeRepository.findById(empId);

        if (user != null && optDept.isPresent() && optEmp.isPresent()) {
            if (canEditDept(optDept.get(), user)) {
                Employee emp = optEmp.get();
                emp.setDepartement(optDept.get());
                employeeRepository.save(emp);
            }
        }
        return "redirect:/departements?selectedDeptId=" + deptId;
    }
}