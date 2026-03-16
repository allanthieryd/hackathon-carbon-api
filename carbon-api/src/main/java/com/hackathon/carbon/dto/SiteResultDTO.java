package com.hackathon.carbon.dto;

import lombok.Data;

@Data
public class SiteResultDTO {
    private Long id;
    private String name;
    private String location;
    private Double superficie;
    private Integer nbParking;
    private Double consoEnergetique;
    private Integer nbEmployes;

    // Matériaux
    private Double quantiteBeton;
    private Double quantiteAcier;
    private Double quantiteVerre;
    private Double quantiteBois;

    // Résultats CO₂
    private Double co2Construction;   // kgCO₂e
    private Double co2Exploitation;   // kgCO₂e
    private Double co2Total;          // kgCO₂e
    private Double co2ParM2;          // kgCO₂e/m²
    private Double co2ParEmploye;     // kgCO₂e/employé
}