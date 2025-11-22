package com.projet.gestion_rh.model.utils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private int idRole;

    @Column(name = "nom_role", unique = true, nullable = false)
    private String nomRole;

    public Role() {}

    public Role(String nomRole) {
        this.nomRole = nomRole;
    }

    // Getters / Setters
    public int getIdRole() { return idRole; }
    public void setIdRole(int idRole) { this.idRole = idRole; }
    public String getNomRole() { return nomRole; }
    public void setNomRole(String nomRole) { this.nomRole = nomRole; }
}