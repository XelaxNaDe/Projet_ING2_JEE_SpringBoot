package com.projet.gestion_rh.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.projet.gestion_rh.model.utils.IntStringPayroll;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Payroll")
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_payroll")
    private int idPayroll;

    
    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int salary;

    @Column(name = "net_pay", nullable = false)
    private double netPay = 0.0;

    @OneToMany(mappedBy = "payroll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IntStringPayroll> lines = new ArrayList<>();

    public Payroll() {}

    public Payroll(Employee employee, LocalDate date, int salary, double netPay) {
        this.employee = employee;
        this.date = date;
        this.salary = salary;
        this.netPay = netPay;
    }

    // Getters / Setters
    public int getIdPayroll() { return idPayroll; }
    public void setIdPayroll(int idPayroll) { this.idPayroll = idPayroll; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getSalary() { return salary; }
    public void setSalary(int salary) { this.salary = salary; }
    public double getNetPay() { return netPay; }
    public void setNetPay(double netPay) { this.netPay = netPay; }
    public List<IntStringPayroll> getLines() { return lines; }
    public void setLines(List<IntStringPayroll> lines) { this.lines = lines; }
}
