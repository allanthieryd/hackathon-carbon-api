package com.hackathon.carbon.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sites")
@Data
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;

    // Caractéristiques physiques
    private Double superficie;       // m²
    private Integer nbParking;
    private Double consoEnergetique; // MWh/an
    private Integer nbEmployes;

    // Matériaux (en tonnes)
    private Double quantiteBeton;
    private Double quantiteAcier;
    private Double quantiteVerre;
    private Double quantiteBois;

    // Résultats calculés
    private Double co2Construction;
    private Double co2Exploitation;
    private Double co2Total;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}