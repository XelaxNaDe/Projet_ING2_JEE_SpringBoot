package com.projet.gestion_rh.controller;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.repository.DepartementRepository;
import com.projet.gestion_rh.repository.EmployeeRepository;
import com.projet.gestion_rh.repository.PayrollRepository;
import com.projet.gestion_rh.repository.ProjetRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    private final DepartementRepository departementRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjetRepository projetRepository;
    private final PayrollRepository payrollRepository;
    private final PasswordEncoder passwordEncoder;
    

    public DashboardController(DepartementRepository dr, EmployeeRepository er, ProjetRepository pr, PayrollRepository payR, PasswordEncoder passwordEncoder) {
        this.departementRepository = dr;
        this.employeeRepository = er;
        this.projetRepository = pr;
        this.payrollRepository = payR;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "connexion"; // Cherche src/main/resources/templates/login.html
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, 
                               @RequestParam String password, 
                               HttpSession session, 
                               Model model) {
        

        Optional<Employee> empOpt = employeeRepository.findByEmail(email);

        if (empOpt.isPresent()) {
            Employee emp = empOpt.get();
            
            if (passwordEncoder.matches(password, emp.getPassword())) {
                session.setAttribute("currentUser", emp);
                return "redirect:/";
            
            } else {
                System.out.println("Mot de passe incorrect");
            }
        } else {
            System.out.println("Email introuvable");
        }

        model.addAttribute("errorMessage", "Email ou mot de passe incorrect.");
        return "connexion";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login";
        }

        model.addAttribute("departementCount", departementRepository.count());
        model.addAttribute("employeeCount", employeeRepository.count());
        model.addAttribute("projetCount", projetRepository.count());
        model.addAttribute("payrollCount", payrollRepository.count());
        
        return "dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Employee currentUser = (Employee) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        Optional<Employee> empOpt = employeeRepository.findById(currentUser.getId());
        if (empOpt.isPresent()) {
            model.addAttribute("employee", empOpt.get());
            return "profile";
        }
        
        return "redirect:/login";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String email,
                                @RequestParam(required = false) String password,
                                HttpSession session,
                                Model model) {

        Employee currentUser = (Employee) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        Employee empToUpdate = employeeRepository.findById(currentUser.getId()).orElse(null);
        if (empToUpdate == null) return "redirect:/login";

        if (!email.equals(empToUpdate.getEmail())) {
            Optional<Employee> existing = employeeRepository.findByEmail(email);
            if (existing.isPresent()) {
                model.addAttribute("error", "Email deja utilisé");
                model.addAttribute("employee", empToUpdate);
                return "profile";
            }
            empToUpdate.setEmail(email);
        }

        if (password != null && !password.isBlank()) {
            empToUpdate.setPassword(passwordEncoder.encode(password));
        }
        employeeRepository.save(empToUpdate);

        session.setAttribute("currentUser", empToUpdate);

        model.addAttribute("success", "Profil mis à jour");
        model.addAttribute("employee", empToUpdate);
        
        return "profile";
    }
}
