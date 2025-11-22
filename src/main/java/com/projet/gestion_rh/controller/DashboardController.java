package com.projet.gestion_rh.controller;

import com.projet.gestion_rh.model.Departement;
import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class    DashboardController {

    private final DepartementRepository departementRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjetRepository projetRepository;
    private final PayrollRepository payrollRepository;
    private final RoleRepository roleRepository;
    private final IntStringPayrollRepository intStringPayrollRepository;

    public DashboardController(DepartementRepository departementRepository,
                               EmployeeRepository employeeRepository,
                               ProjetRepository projetRepository,
                               PayrollRepository payrollRepository,
                               RoleRepository roleRepository,
                               IntStringPayrollRepository intStringPayrollRepository) {
        this.departementRepository = departementRepository;
        this.employeeRepository = employeeRepository;
        this.projetRepository = projetRepository;
        this.payrollRepository = payrollRepository;
        this.roleRepository = roleRepository;
        this.intStringPayrollRepository = intStringPayrollRepository;
    }

    // 🏠 PAGE D'ACCUEIL
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("departementCount", departementRepository.count());
        model.addAttribute("employeeCount", employeeRepository.count());
        model.addAttribute("projetCount", projetRepository.count());
        model.addAttribute("payrollCount", payrollRepository.count());
        return "dashboard"; // dashboard.html
    }

    // 🏢 PAGE DEPARTEMENTS
    @GetMapping("/departements")
    public String departements(@RequestParam(name = "selectedDeptId", required = false) Integer selectedDeptId,
                               Model model) {

        model.addAttribute("departements", departementRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());

        // Si un département est sélectionné, on affiche ses membres
        if (selectedDeptId != null) {
            Optional<Departement> optDept = departementRepository.findById(selectedDeptId);
            if (optDept.isPresent()) {
                Departement d = optDept.get();
                List<Employee> members = employeeRepository.findByDepartement(d);
                model.addAttribute("selectedDepartement", d);
                model.addAttribute("members", members);
            }
        }

        return "departements"; // departements.html
    }

    // ➜ Ajouter un département
    @PostMapping("/departements/add")
    public String addDepartement(@RequestParam String nomDepartement,
                                 @RequestParam(required = false) Integer idChefDepartement) {

        Departement d = new Departement();
        d.setNomDepartement(nomDepartement);
        d.setIdChefDepartement(idChefDepartement);
        departementRepository.save(d);

        return "redirect:/departements";
    }

    // ➜ Affecter un employé à un département
    @PostMapping("/departements/assign")
    public String assignEmployeeToDepartement(@RequestParam int deptId,
                                              @RequestParam int empId) {

        Optional<Departement> optDept = departementRepository.findById(deptId);
        Optional<Employee> optEmp = employeeRepository.findById(empId);

        if (optDept.isPresent() && optEmp.isPresent()) {
            Employee emp = optEmp.get();
            emp.setDepartement(optDept.get());
            employeeRepository.save(emp);
        }

        return "redirect:/departements?selectedDeptId=" + deptId;
    }

    // 👥 PAGE EMPLOYES
    @GetMapping("/employees")
    public String employees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("departements", departementRepository.findAll());
        return "employees"; // employees.html
    }

    // ➜ Ajouter un employé
    @PostMapping("/employees/add")
    public String addEmployee(@RequestParam String fname,
                              @RequestParam String sname,
                              @RequestParam(required = false) String gender,
                              @RequestParam String email,
                              @RequestParam(required = false) String position,
                              @RequestParam(required = false) String grade,
                              @RequestParam(required = false) Integer departementId) {

        Employee e = new Employee();
        e.setFname(fname);
        e.setSname(sname);
        e.setGender(gender);
        e.setEmail(email);
        e.setPassword("password"); // à sécuriser plus tard
        e.setPosition(position);
        e.setGrade(grade);

        if (departementId != null) {
            departementRepository.findById(departementId)
                    .ifPresent(e::setDepartement);
        }

        employeeRepository.save(e);
        return "redirect:/employees";
    }

    // ➜ Supprimer un employé
    @PostMapping("/employees/delete")
    public String deleteEmployee(@RequestParam int id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
        }
        return "redirect:/employees";
    }
}
