package com.projet.gestion_rh.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Departement")
public class Departement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_departement")
    private int idDepartement;

    @Column(name = "nom_departement", nullable = false)
    private String nomDepartement;

    @Column(name = "id_chef_departement")
    private Integer idChefDepartement; // Peut être NULL dans le SQL

    public Departement() {}

    public Departement(String nomDepartement, Integer idChefDepartement) {
        this.nomDepartement = nomDepartement;
        this.idChefDepartement = idChefDepartement;
    }

    // Getters / Setters
    public int getIdDepartement() { return idDepartement; }
    public void setIdDepartement(int idDepartement) { this.idDepartement = idDepartement; }
    public String getNomDepartement() { return nomDepartement; }
    public void setNomDepartement(String nomDepartement) { this.nomDepartement = nomDepartement; }
    public Integer getIdChefDepartement() { return idChefDepartement; }
    public void setIdChefDepartement(Integer idChefDepartement) { this.idChefDepartement = idChefDepartement; }
}