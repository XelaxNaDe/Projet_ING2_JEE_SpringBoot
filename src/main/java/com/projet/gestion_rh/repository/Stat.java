package com.projet.gestion_rh.repository;

// Une interface simple pour récupérer les données "Label + Nombre"
public interface Stat {
    String getLabel();
    Long getValeur();
}