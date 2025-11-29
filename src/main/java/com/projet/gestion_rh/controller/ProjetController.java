package com.projet.gestion_rh.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.model.Projet;
import com.projet.gestion_rh.repository.EmployeeRepository;
import com.projet.gestion_rh.repository.ProjetRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProjetController {

    private final ProjetRepository projetRepository;
    private final EmployeeRepository employeeRepository;

    public ProjetController(ProjetRepository projetRepository, EmployeeRepository employeeRepository) {
        this.projetRepository = projetRepository;
        this.employeeRepository = employeeRepository;
    }

    // --- MÉTHODE PRIVÉE DE SÉCURITÉ ---
    // Vérifie si l'utilisateur a le droit de modifier CE projet spécifique
    private boolean canEditProject(Projet p, Employee user) {
        // 1. L'Admin a tous les droits
        if (user.hasRole("ADMINISTRATOR")) return true;
        
        // 2. Le Chef de Projet a les droits SUR SES PROJETS
        if (user.hasRole("PROJECTMANAGER")) {
            return p.getChefProjet() != null && p.getChefProjet().getId() == user.getId();
        }
        
        return false; // Sinon, refusé
    }

    // LECTURE (Accessible à tous les employés connectés)
    @GetMapping("/projets")
    public String projets(@RequestParam(required = false) Integer id,
                          @RequestParam(required = false) Integer editId,
                          Model model, HttpSession session) {
        
        if (session.getAttribute("currentUser") == null) return "redirect:/login";

        model.addAttribute("projets", projetRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());

        if (id != null) {
            projetRepository.findById(id).ifPresent(p -> {
                model.addAttribute("selectedProjet", p);
                model.addAttribute("equipe", p.getEquipe());
            });
        }

        if (editId != null) {
            projetRepository.findById(editId).ifPresent(p -> model.addAttribute("projetToEdit", p));
        }
        return "projets";
    }

    // AJOUT (Seulement Admin ou n'importe quel Project Manager)
    @PostMapping("/projets/add")
    public String addProjet(@RequestParam String nomProjet,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
                            @RequestParam(required = false) Integer chefProjetId,
                            @RequestParam String etat,
                            HttpSession session) {

        Employee user = (Employee) session.getAttribute("currentUser");
        // Sécurité : Faut être Admin ou Chef de projet pour créer
        if (user == null || (!user.hasRole("ADMINISTRATOR") && !user.hasRole("PROJECTMANAGER"))) {
            return "redirect:/projets?error=forbidden";
        }

        Projet p = new Projet();
        saveProjetData(p, nomProjet, dateDebut, dateFin, chefProjetId, etat);
        return "redirect:/projets";
    }

    // MODIFICATION (Admin ou Le Chef du projet)
    @PostMapping("/projets/update")
    public String updateProjet(@RequestParam Integer id,
                               @RequestParam String nomProjet,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
                               @RequestParam(required = false) Integer chefProjetId,
                               @RequestParam String etat,
                               HttpSession session) {

        Employee user = (Employee) session.getAttribute("currentUser");
        Optional<Projet> optP = projetRepository.findById(id);

        if (user != null && optP.isPresent()) {
            Projet p = optP.get();
            // VÉRIFICATION DES DROITS
            if (canEditProject(p, user)) {
                saveProjetData(p, nomProjet, dateDebut, dateFin, chefProjetId, etat);
            }
        }
        return "redirect:/projets";
    }

    private void saveProjetData(Projet p, String nom, LocalDate debut, LocalDate fin, Integer chefId, String etat) {
        p.setNomProjet(nom);
        p.setDateDebut(debut);
        p.setDateFin(fin);
        p.setEtat(etat);

        Projet savedProjet = projetRepository.save(p);

        if (chefId != null) {
            Optional<Employee> chefOptional = employeeRepository.findById(chefId);
            
            if (chefOptional.isPresent()) {
                Employee chef = chefOptional.get();
                
                savedProjet.setChefProjet(chef);
                projetRepository.save(savedProjet); // Mise à jour de la relation chef

                boolean inTeam = chef.getProjets().stream()
                        .anyMatch(proj -> proj.getIdProjet() == savedProjet.getIdProjet());

                if (!inTeam) {
                    chef.getProjets().add(savedProjet);
                    employeeRepository.save(chef); 
                }
            }
        } else {
            savedProjet.setChefProjet(null);
            projetRepository.save(savedProjet);
        }
    }

    // AFFECTATION (Admin ou Le Chef du projet)
    @PostMapping("/projets/assign")
    public String assignToProjet(@RequestParam int projetId, @RequestParam int empId, HttpSession session) {
        Employee user = (Employee) session.getAttribute("currentUser");
        Optional<Projet> optP = projetRepository.findById(projetId);
        Optional<Employee> optE = employeeRepository.findById(empId);

        if (user != null && optP.isPresent() && optE.isPresent()) {
            if (canEditProject(optP.get(), user)) {
                Employee e = optE.get();
                e.getProjets().add(optP.get()); // MAJ relation ManyToMany
                employeeRepository.save(e);
            }
        }
        return "redirect:/projets?id=" + projetId;
    }

    // RETIRER UN MEMBRE (Admin ou Le Chef du projet)
    @PostMapping("/projets/unassign")
    public String unassignFromProjet(@RequestParam int projetId, 
                                     @RequestParam int empId, 
                                     HttpSession session) {
        
        Employee user = (Employee) session.getAttribute("currentUser");
        Optional<Projet> optP = projetRepository.findById(projetId);
        Optional<Employee> optE = employeeRepository.findById(empId);

        if (user != null && optP.isPresent() && optE.isPresent()) {
            // Vérification sécurité
            if (canEditProject(optP.get(), user)) {
                Employee e = optE.get();
                // On retire le projet de la liste de l'employé
                e.getProjets().remove(optP.get());
                // On sauvegarde l'employé (propriétaire de la relation)
                employeeRepository.save(e);
            }
        }
        return "redirect:/projets?id=" + projetId;
    }

    // SUPPRESSION (Admin ou Le Chef du projet)
    @PostMapping("/projets/delete")
    public String deleteProjet(@RequestParam int id, HttpSession session) {
        Employee user = (Employee) session.getAttribute("currentUser");
        Optional<Projet> optP = projetRepository.findById(id);

        if (user != null && optP.isPresent()) {
            if (canEditProject(optP.get(), user)) {
                projetRepository.deleteById(id);
            }
        }
        return "redirect:/projets";
    }
}