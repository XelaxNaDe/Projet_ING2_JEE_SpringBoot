package com.projet.gestion_rh.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Projet")
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projet")
    private int idProjet;

    @Column(name = "nom_projet", nullable = false)
    private String nomProjet;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(columnDefinition = "ENUM('En cours', 'Terminé', 'Annulé')")
    private String etat;

    // Le Chef de projet (ManyToOne car un employé peut gérer plusieurs projets)
    @ManyToOne
    @JoinColumn(name = "id_chef_projet")
    private Employee chefProjet;

    // L'équipe (Relation inverse de celle dans Employee)
    @ManyToMany(mappedBy = "projets")
    private Set<Employee> equipe = new HashSet<>();

    public Projet() {}

    public Projet(String nomProjet, LocalDate dateDebut, LocalDate dateFin, Employee chefProjet, String etat) {
        this.nomProjet = nomProjet;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.chefProjet = chefProjet;
        this.etat = etat;
    }

    // Getters / Setters
    public int getIdProjet() { return idProjet; }
    public void setIdProjet(int idProjet) { this.idProjet = idProjet; }
    public String getNomProjet() { return nomProjet; }
    public void setNomProjet(String nomProjet) { this.nomProjet = nomProjet; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public Employee getChefProjet() { return chefProjet; }
    public void setChefProjet(Employee chefProjet) { this.chefProjet = chefProjet; }
    public Set<Employee> getEquipe() { return equipe; }
    public void setEquipe(Set<Employee> equipe) { this.equipe = equipe; }
}