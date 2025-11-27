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

    // Repositories nécessaires pour le Dashboard
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

    // AFFICHER LA PAGE DE LOGIN
    @GetMapping("/login")
    public String showLoginForm() {
        return "connexion"; // Cherche src/main/resources/templates/login.html
    }

    // TRAITER LE FORMULAIRE DE LOGIN
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

    // DÉCONNEXION
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // Sécurité : Si pas connecté, on redirige vers le login
        // (Comme c'est le même contrôleur, Spring trouve le chemin tout de suite)
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login";
        }

        // Chargement des statistiques pour le dashboard
        model.addAttribute("departementCount", departementRepository.count());
        model.addAttribute("employeeCount", employeeRepository.count());
        model.addAttribute("projetCount", projetRepository.count());
        model.addAttribute("payrollCount", payrollRepository.count());
        
        return "dashboard"; // Cherche src/main/resources/templates/dashboard.html
    }

    // 1. Afficher la page Profil
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Employee currentUser = (Employee) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        // On recharge les données depuis la BDD pour être sûr d'avoir la version à jour
        Optional<Employee> empOpt = employeeRepository.findById(currentUser.getId());
        if (empOpt.isPresent()) {
            model.addAttribute("employee", empOpt.get());
            return "profile"; // Cherche profile.html
        }
        
        return "redirect:/login";
    }

    // 2. Traiter la mise à jour
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String email,
                                @RequestParam(required = false) String password,
                                HttpSession session,
                                Model model) {

        Employee currentUser = (Employee) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        // On récupère l'employé réel en base
        Employee empToUpdate = employeeRepository.findById(currentUser.getId()).orElse(null);
        if (empToUpdate == null) return "redirect:/login";

        // --- Vérification Email Unique ---
        // Si l'email change, on vérifie qu'il n'est pas déjà pris par un autre employé
        if (!email.equals(empToUpdate.getEmail())) {
            Optional<Employee> existing = employeeRepository.findByEmail(email);
            if (existing.isPresent()) {
                model.addAttribute("error", "Email deja utilisé");
                model.addAttribute("employee", empToUpdate);
                return "profile";
            }
            empToUpdate.setEmail(email);
        }

        // --- Changement de mot de passe si rempli ---
        if (password != null && !password.isBlank()) {
            empToUpdate.setPassword(passwordEncoder.encode(password));
        }

        // Sauvegarde
        employeeRepository.save(empToUpdate);

        // On met à jour la session avec les nouvelles infos
        session.setAttribute("currentUser", empToUpdate);

        model.addAttribute("success", "Profil mis à jour");
        model.addAttribute("employee", empToUpdate);
        
        return "profile";
    }
}