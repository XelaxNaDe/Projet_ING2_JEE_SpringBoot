package com.projet.gestion_rh.controller;

import com.projet.gestion_rh.model.Payroll;
import com.projet.gestion_rh.model.utils.IntStringPayroll;
import com.projet.gestion_rh.repository.EmployeeRepository;
import com.projet.gestion_rh.repository.IntStringPayrollRepository;
import com.projet.gestion_rh.repository.PayrollRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class PayrollController {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final IntStringPayrollRepository lineRepository;

    public PayrollController(PayrollRepository payrollRepository,
                             EmployeeRepository employeeRepository,
                             IntStringPayrollRepository lineRepository) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
        this.lineRepository = lineRepository;
    }

    @GetMapping("/payrolls")
    public String payrolls(@RequestParam(required = false) Integer id,
                           Model model) {

        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("payrolls", payrollRepository.findAll());

        if (id != null) {
            Optional<Payroll> p = payrollRepository.findById(id);
            if (p.isPresent()) {
                model.addAttribute("selectedPayroll", p.get());
                model.addAttribute("lines", p.get().getLines());
            }
        }

        return "payrolls";
    }

    @PostMapping("/payrolls/add")
    public String addPayroll(@RequestParam int empId,
                             @RequestParam double salary,
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        var opt = employeeRepository.findById(empId);

        if (opt.isPresent()) {
            Payroll p = new Payroll();
            p.setEmployee(opt.get());
            p.setSalary((int) Math.round(salary));
            p.setDate(date);

            // c'est ca qui manquait :
            p.setNetPay((int) Math.round(salary));

            payrollRepository.save(p);
        }

        return "redirect:/payrolls";
    }

    @PostMapping("/payrolls/add-line")
    public String addLine(@RequestParam int payrollId,
                          @RequestParam String label,
                          @RequestParam double amount,
                          @RequestParam String typeList) {

        Optional<Payroll> opt = payrollRepository.findById(payrollId);
        if (opt.isPresent()) {
            Payroll p = opt.get();

            IntStringPayroll line = new IntStringPayroll();
            line.setLabel(label);
            line.setAmount((int) Math.round(amount)); // je fait ca parce que le amount est en int de base et ici je l'ai mis en double
            line.setTypeList(typeList);
            line.setPayroll(p);

            lineRepository.save(line);

            if (typeList.equals("Prime")) {
                p.setNetPay(p.getNetPay() + (int) Math.round(amount));
            } else {
                p.setNetPay(p.getNetPay() - (int) Math.round(amount));
            }


            payrollRepository.save(p);
        }

        return "redirect:/payrolls?id=" + payrollId;
    }

    @PostMapping("/payrolls/delete")
    public String deletePayroll(@RequestParam int id) {
        if (payrollRepository.existsById(id)) {
            payrollRepository.deleteById(id);
        }
        return "redirect:/payrolls";
    }
}
