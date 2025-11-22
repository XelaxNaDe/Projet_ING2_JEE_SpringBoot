package com.projet.gestion_rh.model.utils;

import com.projet.gestion_rh.model.Payroll;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "IntStringPayroll")
public class IntStringPayroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_line")
    private int idLine;

    @Column(nullable = false)
    private int amount;

    private String label;

    @Column(name = "type_list")
    private String typeList; // "Prime" ou "Déduction"

    // Liaison vers la fiche de paie parente
    @ManyToOne
    @JoinColumn(name = "id_payroll", nullable = false)
    private Payroll payroll;

    public IntStringPayroll() {}

    public IntStringPayroll(int amount, String label, String typeList, Payroll payroll) {
        this.amount = amount;
        this.label = label;
        this.typeList = typeList;
        this.payroll = payroll;
    }

    // Getters / Setters
    public int getIdLine() { return idLine; }
    public void setIdLine(int idLine) { this.idLine = idLine; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getTypeList() { return typeList; }
    public void setTypeList(String typeList) { this.typeList = typeList; }
    public Payroll getPayroll() { return payroll; }
    public void setPayroll(Payroll payroll) { this.payroll = payroll; }
}