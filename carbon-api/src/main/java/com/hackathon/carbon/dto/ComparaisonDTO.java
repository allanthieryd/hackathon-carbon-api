package com.hackathon.carbon.dto;

import lombok.Data;
import java.util.List;

@Data
public class ComparaisonDTO {
    private List<SiteResultDTO> sites;
    private SiteResultDTO meilleurCo2Total;
    private SiteResultDTO meilleurCo2ParM2;
    private SiteResultDTO meilleurCo2ParEmploye;
    private Double moyenneCo2Total;
    private Double moyenneCo2ParM2;
    private Double moyenneCo2ParEmploye;
}