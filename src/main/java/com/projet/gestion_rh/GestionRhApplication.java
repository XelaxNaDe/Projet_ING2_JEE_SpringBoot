package com.projet.gestion_rh;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.projet.gestion_rh.model.Employee;
import com.projet.gestion_rh.repository.EmployeeRepository;

@SpringBootApplication
@ComponentScan(basePackages = "com.projet")
public class GestionRhApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionRhApplication.class, args);
    }

    @Bean
    public CommandLineRunner migrationMotsDePasse(EmployeeRepository repo, PasswordEncoder encoder) {
        return args -> {
            List<Employee> employees = repo.findAll();
            for (Employee e : employees) {
                // Si le mot de passe n'est pas encore haché (BCrypt commence toujours par $2a$)
                if (!e.getPassword().startsWith("$2a$")) {
                    String nouveauMdp = encoder.encode(e.getPassword());
                    e.setPassword(nouveauMdp);
                    repo.save(e);
                    System.out.println(" Migration du mot de passe pour : " + e.getEmail());
                }
            }
        };
    }
}