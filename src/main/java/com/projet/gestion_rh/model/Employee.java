package com.projet.gestion_rh.model;

import com.projet.gestion_rh.model.utils.Role;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fname;
    private String sname;
    private String gender;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String password;
    private String position;
    private String grade;

    // Relation vers Departement (Clé étrangère id_departement)
    @ManyToOne
    @JoinColumn(name = "id_departement")
    private Departement departement;

    // Relation ManyToMany vers Role (Table de jointure Employee_Role)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "Employee_Role",
        joinColumns = @JoinColumn(name = "id"),
        inverseJoinColumns = @JoinColumn(name = "id_role")
    )
    private Set<Role> roles = new HashSet<>();

    // Relation ManyToMany vers Projet (Table de jointure Employe_Projet)
    @ManyToMany
    @JoinTable(
        name = "Employe_Projet",
        joinColumns = @JoinColumn(name = "id"),
        inverseJoinColumns = @JoinColumn(name = "id_projet")
    )
    private Set<Projet> projets = new HashSet<>();

    public Employee() {}

    // Constructeur utile pour l'inscription
    public Employee(String fname, String sname, String email, String password, Departement departement) {
        this.fname = fname;
        this.sname = sname;
        this.email = email;
        this.password = password;
        this.departement = departement;
    }

    // Getters / Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFname() { return fname; }
    public void setFname(String fname) { this.fname = fname; }
    public String getSname() { return sname; }
    public void setSname(String sname) { this.sname = sname; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    
    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement departement) { this.departement = departement; }
    
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    
    public Set<Projet> getProjets() { return projets; }
    public void setProjets(Set<Projet> projets) { this.projets = projets; }
}