package com.projet.gestion_rh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.projet")
public class GestionRhApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionRhApplication.class, args);
    }

}