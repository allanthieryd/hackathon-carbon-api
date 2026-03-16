package com.hackathon.carbon.dto;

import lombok.Data;

@Data
public class SiteDTO {
    private String name;
    private String location;
    private Double superficie;
    private Integer nbParking;
    private Double consoEnergetique;
    private Integer nbEmployes;
    private Double quantiteBeton;
    private Double quantiteAcier;
    private Double quantiteVerre;
    private Double quantiteBois;
}