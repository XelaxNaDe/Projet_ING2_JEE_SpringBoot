package com.projet.gestion_rh.controller;

import com.projet.gestion_rh.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

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

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("departements", departementRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("projets", projetRepository.findAll());
        model.addAttribute("payrolls", payrollRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("payrollLines", intStringPayrollRepository.findAll());

        return "dashboard"; // => dashboard.html dans /templates
    }
}
