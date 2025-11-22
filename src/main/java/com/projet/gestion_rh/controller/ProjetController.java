package com.projet.gestion_rh.controller;

import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.model.Projet;
import com.projet.gestion_rh.repository.EmployeeRepository;
import com.projet.gestion_rh.repository.ProjetRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class ProjetController {

    private final ProjetRepository projetRepository;
    private final EmployeeRepository employeeRepository;

    public ProjetController(ProjetRepository projetRepository,
                            EmployeeRepository employeeRepository) {
        this.projetRepository = projetRepository;
        this.employeeRepository = employeeRepository;
    }

    // PAGE PROJETS
    @GetMapping("/projets")
    public String projets(@RequestParam(required = false) Integer id,
                          Model model) {

        model.addAttribute("projets", projetRepository.findAll());
        model.addAttribute("employees", employeeRepository.findAll());

        if (id != null) {
            Optional<Projet> opt = projetRepository.findById(id);
            if (opt.isPresent()) {
                Projet p = opt.get();
                model.addAttribute("selectedProjet", p);
                model.addAttribute("equipe", p.getEquipe());
            }
        }

        return "projets";
    }

    // Ajouter un projet
    @PostMapping("/projets/add")
    public String addProjet(@RequestParam String nomProjet,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
                            @RequestParam(required = false) Integer chefProjetId,
                            @RequestParam String etat) {

        Projet p = new Projet();
        p.setNomProjet(nomProjet);
        p.setDateDebut(dateDebut);   // ✅ maintenant c'est un LocalDate
        p.setDateFin(dateFin);       // ✅ idem
        p.setEtat(etat);

        if (chefProjetId != null) {
            employeeRepository.findById(chefProjetId).ifPresent(p::setChefProjet);
        }

        projetRepository.save(p);
        return "redirect:/projets";
    }

    // Affecter un employé à un projet
    @PostMapping("/projets/assign")
    public String assignToProjet(@RequestParam int projetId,
                                 @RequestParam int empId) {

        Optional<Projet> optP = projetRepository.findById(projetId);
        Optional<Employee> optE = employeeRepository.findById(empId);

        if (optP.isPresent() && optE.isPresent()) {
            Projet p = optP.get();
            p.getEquipe().add(optE.get());
            projetRepository.save(p);
        }

        return "redirect:/projets?id=" + projetId;
    }

    // Supprimer un projet
    @PostMapping("/projets/delete")
    public String deleteProjet(@RequestParam int id) {

        if (projetRepository.existsById(id)) {
            projetRepository.deleteById(id);
        }

        return "redirect:/projets";
    }
}
