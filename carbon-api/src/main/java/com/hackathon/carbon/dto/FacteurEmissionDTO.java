package com.hackathon.carbon.dto;

import lombok.Data;

@Data
public class FacteurEmissionDTO {
    private String materiau;
    private Double facteur;
    private String unite;
    private String source;
    private String version;
}