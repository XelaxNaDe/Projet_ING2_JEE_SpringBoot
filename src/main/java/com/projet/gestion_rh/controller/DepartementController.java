package com.projet.gestion_rh.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projet.gestion_rh.model.Departement;
import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.repository.DepartementRepository;
import com.projet.gestion_rh.repository.EmployeeRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class DepartementController {

    private final DepartementRepository departementRepository;
    private final EmployeeRepository employeeRepository;

    public DepartementController(DepartementRepository departementRepository, EmployeeRepository employeeRepository) {
        this.departementRepository = departementRepository;
        this.employeeRepository = employeeRepository;
    }

    private boolean isDeptChief(Departement d, Employee user) {
        return d.getIdChefDepartement() != null
                && d.getIdChefDepartement().equals(user.getId());
    }

    private boolean canEditDept(Departement d, Employee user) {
        // ADMIN peut tout
        if (user.hasRole("ADMINISTRATOR")) return true;

        // Chef identifié par id_chef_departement (pas besoin de rôle HEADDEPARTEMENT)
        return isDeptChief(d, user);
    }


    // Fonction pour sauvegarder le département + assigner le chef
    private void saveDepartementData(Departement d, String nom, Integer idChef) {
        d.setNomDepartement(nom);
        d.setIdChefDepartement(idChef);

        Departement savedDept = departementRepository.save(d);

        if (idChef != null) {
            Optional<Employee> optChef = employeeRepository.findById(idChef);
            if (optChef.isPresent()) {
                Employee chef = optChef.get();
                chef.setDepartement(savedDept);
                employeeRepository.save(chef);
            }
        }
    }

    // PAGE PRINCIPALE
    @GetMapping("/departements")
    public String departements(
            @RequestParam(name = "selectedDeptId", required = false) Integer selectedDeptId,
            Model model, HttpSession session) {

        Employee currentUser = (Employee) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        boolean isAdmin = currentUser.hasRole("ADMINISTRATOR");

        boolean isDeptChiefSomewhere = departementRepository.findAll().stream()
                .anyMatch(d -> d.getIdChefDepartement() != null
                        && d.getIdChefDepartement().equals(currentUser.getId()));

        boolean isHeadOrAdmin = isAdmin || isDeptChiefSomewhere;

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isHeadOrAdmin", isHeadOrAdmin);

        model.addAttribute("departements", departementRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());

        model.addAttribute("selectedDepartement", null);
        model.addAttribute("members", List.of());
        model.addAttribute("canManageSelectedDept", false); // par défaut

        if (selectedDeptId != null) {
            Optional<Departement> optDept = departementRepository.findById(selectedDeptId);
            if (optDept.isPresent()) {
                Departement d = optDept.get();
                List<Employee> members = employeeRepository.findByDepartement(d);
                if (members == null) members = List.of();

                boolean canManage = canEditDept(d, currentUser); // admin OU chef de ce département

                model.addAttribute("selectedDepartement", d);
                model.addAttribute("members", members);
                model.addAttribute("canManageSelectedDept", canManage);
            }
        }

        return "departements";
    }

    // AJOUTER UN DEPARTEMENT (ADMIN)
    @PostMapping("/departements/add")
    public String addDepartement(@RequestParam String nomDepartement,
                                 @RequestParam(required = false) Integer idChefDepartement,
                                 HttpSession session) {

        Employee user = (Employee) session.getAttribute("currentUser");

        if (user != null && user.hasRole("ADMINISTRATOR")) {
            Departement d = new Departement();
            saveDepartementData(d, nomDepartement, idChefDepartement);
        }
        return "redirect:/departements";
    }

    @PostMapping("/departements/delete")
    public String deleteDepartement(@RequestParam int deptId, HttpSession session) {

        Employee user = (Employee) session.getAttribute("currentUser");
        Optional<Departement> optDept = departementRepository.findById(deptId);

        if (user != null && user.hasRole("ADMINISTRATOR") && optDept.isPresent()) {
            Departement d = optDept.get();

            // Détacher tous les employés de ce département
            List<Employee> members = employeeRepository.findByDepartement(d);
            for (Employee emp : members) {
                emp.setDepartement(null);
                employeeRepository.save(emp);
            }

            // Supprimer le département
            departementRepository.delete(d);
        }

        return "redirect:/departements";
    }



    // ASSIGNER EMPLOYE A UN DEPARTEMENT
    @PostMapping("/departements/assign")
    public String assignEmployeeToDepartement(@RequestParam int deptId,
                                              @RequestParam int empId,
                                              HttpSession session) {

        Employee user = (Employee) session.getAttribute("currentUser");
        Optional<Departement> optDept = departementRepository.findById(deptId);
        Optional<Employee> optEmp = employeeRepository.findById(empId);

        if (user != null && optDept.isPresent() && optEmp.isPresent()) {
            Departement d = optDept.get();

            if (canEditDept(d, user)) {
                Employee emp = optEmp.get();
                emp.setDepartement(d);
                employeeRepository.save(emp);
            }
        }
        return "redirect:/departements?selectedDeptId=" + deptId;
    }

    // SUPPRIMER UN MEMBRE DU DEPARTEMENT
    @PostMapping("/departements/removeMember")
    public String removeMember(@RequestParam int empId,
                               @RequestParam int deptId,
                               HttpSession session) {

        Employee user = (Employee) session.getAttribute("currentUser");

        Optional<Departement> optDept = departementRepository.findById(deptId);
        Optional<Employee> optEmp = employeeRepository.findById(empId);

        if (user != null && optDept.isPresent() && optEmp.isPresent()) {
            Departement d = optDept.get();

            if (canEditDept(d, user)) {
                Employee emp = optEmp.get();
                emp.setDepartement(null);
                employeeRepository.save(emp);
            }
        }

        return "redirect:/departements?selectedDeptId=" + deptId;
    }

    // CHANGER LE CHEF DU DEPARTEMENT
    @PostMapping("/departements/setChief")
    public String setDeptChief(@RequestParam int deptId,
                               @RequestParam int idChefDepartement,
                               HttpSession session) {

        Employee user = (Employee) session.getAttribute("currentUser");

        Optional<Departement> optDept = departementRepository.findById(deptId);

        if (user != null && optDept.isPresent()) {
            Departement d = optDept.get();

            if (canEditDept(d, user)) {
                saveDepartementData(d, d.getNomDepartement(), idChefDepartement);
            }
        }

        return "redirect:/departements?selectedDeptId=" + deptId;
    }
}
