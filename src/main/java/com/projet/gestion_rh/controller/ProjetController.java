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
                          @RequestParam(required = false) Integer editId,
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

        if (editId != null){
            projetRepository.findById(editId).ifPresent(p -> model.addAttribute("projetToEdit", p));
        }
        return "projets";
    }

   // AJOUTER UN PROJET
   @PostMapping("/projets/add")
   public String addProjet(@RequestParam String nomProjet,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
                           @RequestParam(required = false) Integer chefProjetId,
                           @RequestParam String etat) {

       Projet p = new Projet();
       saveProjetData(p, nomProjet, dateDebut, dateFin, chefProjetId, etat);
       return "redirect:/projets";
   }

    //Modifier un projet

    @PostMapping("/projets/update")
    public String updateProjet(@RequestParam Integer id,
                               @RequestParam String nomProjet,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin,
                               @RequestParam(required = false) Integer chefProjetId,
                               @RequestParam String etat) {

        projetRepository.findById(id).ifPresent(p -> {
            saveProjetData(p, nomProjet, dateDebut, dateFin, chefProjetId, etat);
        });
        return "redirect:/projets";
    }

    //Eviter la répétition de code avec des copier-coller pour creer et modifier un projet

    private void saveProjetData(Projet p, String nom, LocalDate debut, LocalDate fin, Integer chefId, String etat) {
        p.setNomProjet(nom);
        p.setDateDebut(debut);
        p.setDateFin(fin);
        p.setEtat(etat);

        if (chefId != null) {
            employeeRepository.findById(chefId).ifPresent(p::setChefProjet);
        } else {
            p.setChefProjet(null); // Important si on retire le chef
        }
        projetRepository.save(p);
    }

    // AFFECTER UN EMPLOYÉ 
    @PostMapping("/projets/assign")
    public String assignToProjet(@RequestParam int projetId,
                                 @RequestParam int empId) {

        Optional<Projet> optP = projetRepository.findById(projetId);
        Optional<Employee> optE = employeeRepository.findById(empId);

        if (optP.isPresent() && optE.isPresent()) {
            Projet p = optP.get();
            Employee e = optE.get();
            e.getProjets().add(p);
            employeeRepository.save(e);
        }

        return "redirect:/projets?id=" + projetId;
    }

    // RETIRER UN EMPLOYÉ 
    @PostMapping("/projets/unassign")
    public String unassignFromProjet(@RequestParam int projetId,
                                     @RequestParam int empId) {
        
        Optional<Projet> optP = projetRepository.findById(projetId);
        Optional<Employee> optE = employeeRepository.findById(empId);

        if (optP.isPresent() && optE.isPresent()) {
            Projet p = optP.get();
            Employee e = optE.get();
            e.getProjets().remove(p);
            employeeRepository.save(e);
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
