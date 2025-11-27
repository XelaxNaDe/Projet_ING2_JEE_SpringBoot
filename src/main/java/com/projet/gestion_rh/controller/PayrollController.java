package com.projet.gestion_rh.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.model.Payroll;
import com.projet.gestion_rh.model.utils.IntStringPayroll;
import com.projet.gestion_rh.repository.EmployeeRepository;
import com.projet.gestion_rh.repository.IntStringPayrollRepository;
import com.projet.gestion_rh.repository.PayrollRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PayrollController {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final IntStringPayrollRepository lineRepository;

    public PayrollController(PayrollRepository pRepo, EmployeeRepository eRepo, IntStringPayrollRepository lRepo) {
        this.payrollRepository = pRepo;
        this.employeeRepository = eRepo;
        this.lineRepository = lRepo;
    }

    // ATTENTION : J'ai mis "/payrolls" avec un 's' pour correspondre à ton HTML
    @GetMapping("/payrolls")
    public String payrolls(@RequestParam(required = false) Integer id, 
                           Model model, 
                           HttpSession session) {
        
        Employee user = (Employee) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        // FILTRAGE DES DONNÉES SELON LE RÔLE
        if (user.hasRole("ADMINISTRATOR")) {
            model.addAttribute("payrolls", payrollRepository.findAll());
            model.addAttribute("employees", employeeRepository.findAll());
        } else {
            // Employé -> Voir ses fiches seulement
            model.addAttribute("payrolls", payrollRepository.findByEmployee(user));
            model.addAttribute("employees", List.of()); 
        }

        if (id != null) {
            Optional<Payroll> opt = payrollRepository.findById(id);
            if (opt.isPresent()) {
                Payroll p = opt.get();
                if (user.hasRole("ADMINISTRATOR") || p.getEmployee().getId() == user.getId()) {
                    model.addAttribute("selectedPayroll", p);
                    // CORRECTION : Utilise getDetails() si c'est le nom dans ton Modèle Payroll
                    // Si dans Payroll.java c'est "private List<IntStringPayroll> lines", alors c'est getLines()
                    // Je mets getDetails() car c'était le nom dans ton HTML précédent.
                    model.addAttribute("lines", p.getLines()); 
                }
            }
        }
        return "payrolls"; // Cherche payrolls.html
    }

    @PostMapping("/payrolls/add")
    public String createPayroll(@RequestParam int empId,
                                @RequestParam String date,
                                @RequestParam double salary,
                                HttpSession session) {

        Employee user = (Employee) session.getAttribute("currentUser");
        if (user == null || !user.hasRole("ADMINISTRATOR")) {
            return "redirect:/payrolls";
        }

        Optional<Employee> empOpt = employeeRepository.findById(empId);
        if (empOpt.isEmpty()) return "redirect:/payrolls";

        Payroll p = new Payroll();
        p.setEmployee(empOpt.get());
        p.setDate(LocalDate.parse(date));
        p.setSalary((int) salary);

        // Net = salaire brut (aucune ligne encore)
        p.setNetPay(salary);

        // IMPORTANT : ne rien faire avec les lignes ici !
        payrollRepository.save(p);

        return "redirect:/payrolls?id=" + p.getIdPayroll();
    }


    @PostMapping("/payrolls/add-line")
    public String addLine(@RequestParam int payrollId,
                          @RequestParam String label,
                          @RequestParam double amount,
                          @RequestParam String typeList,
                          HttpSession session) {

        Employee user = (Employee) session.getAttribute("currentUser");
        if (user == null || !user.hasRole("ADMINISTRATOR")) return "redirect:/payrolls";

        Optional<Payroll> optP = payrollRepository.findById(payrollId);
        if (optP.isPresent()) {
            Payroll p = optP.get();

            // 1) Créer la ligne
            IntStringPayroll line = new IntStringPayroll();
            line.setLabel(label);
            line.setTypeList(typeList);
            line.setAmount((int) amount);      // tu es en int dans l’entité
            line.setPayroll(p);

            // 2) Sauvegarder la ligne directement
            lineRepository.save(line);

            // 3) Mettre à jour le net à payer
            double net = p.getNetPay();
            if ("Prime".equalsIgnoreCase(typeList)) {
                net += amount;
            } else if ("Déduction".equalsIgnoreCase(typeList)) {
                net -= amount;
            }
            p.setNetPay(net);

            // 4) Sauvegarder la fiche de paie
            payrollRepository.save(p);
        }
        return "redirect:/payrolls?id=" + payrollId;
    }


    @PostMapping("/payrolls/delete-line")
    public String deleteLine(@RequestParam int lineId,
                             @RequestParam int payrollId,
                             HttpSession session) {

        Employee user = (Employee) session.getAttribute("currentUser");
        if (user == null || !user.hasRole("ADMINISTRATOR")) return "redirect:/payrolls";

        Optional<IntStringPayroll> optLine = lineRepository.findById(lineId);
        if (optLine.isPresent()) {
            IntStringPayroll line = optLine.get();
            Payroll p = line.getPayroll();

            if (p != null && p.getIdPayroll() == payrollId) {

                // 1) Mettre à jour le net avant suppression
                double net = p.getNetPay();
                if ("Prime".equalsIgnoreCase(line.getTypeList())) {
                    // On enlève la prime
                    net -= line.getAmount();
                } else if ("Déduction".equalsIgnoreCase(line.getTypeList())) {
                    // On enlève une déduction -> on rajoute au net
                    net += line.getAmount();
                }
                p.setNetPay(net);

                // 2) Supprimer la ligne
                lineRepository.delete(line);

                // 3) Sauvegarder la fiche de paie
                payrollRepository.save(p);
            }
        }
        return "redirect:/payrolls?id=" + payrollId;
    }



    // AJOUT SUPPRESSION
    @PostMapping("/payrolls/delete")
    public String deletePayroll(@RequestParam int id, HttpSession session) {
        Employee user = (Employee) session.getAttribute("currentUser");
        if (user != null && user.hasRole("ADMINISTRATOR")) {
            payrollRepository.deleteById(id);
        }
        return "redirect:/payrolls";
    }

    @GetMapping("/payrolls/print")
    public String printPayroll(@RequestParam int id, Model model, HttpSession session) {
        Employee user = (Employee) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        Optional<Payroll> opt = payrollRepository.findById(id);
        if (opt.isPresent()) {
            Payroll p = opt.get();
            
            // SÉCURITÉ : On vérifie que c'est bien ma fiche ou que je suis Admin
            if (user.hasRole("ADMINISTRATOR") || p.getEmployee().getId() == user.getId()) {
                model.addAttribute("payroll", p);
                model.addAttribute("lines", p.getLines()); // ou getDetails() selon votre modèle
                return "payroll_print"; // Renvoie vers payroll_print.html
            }
        }
        return "redirect:/payrolls"; // Retour liste si erreur ou fraude
    }
}