package com.hackathon.carbon.service;

import com.hackathon.carbon.dto.ComparaisonDTO;
import com.hackathon.carbon.dto.SiteResultDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComparaisonService {

    private final SiteService siteService;

    public ComparaisonDTO comparerSites(List<Long> ids) {
        List<SiteResultDTO> sites = ids.stream()
                .map(siteService::getSiteById)
                .toList();

        ComparaisonDTO dto = new ComparaisonDTO();
        dto.setSites(sites);

        // Meilleurs sites (CO₂ le plus bas)
        dto.setMeilleurCo2Total(sites.stream()
                .filter(s -> s.getCo2Total() != null)
                .min((a, b) -> Double.compare(a.getCo2Total(), b.getCo2Total()))
                .orElse(null));

        dto.setMeilleurCo2ParM2(sites.stream()
                .filter(s -> s.getCo2ParM2() != null)
                .min((a, b) -> Double.compare(a.getCo2ParM2(), b.getCo2ParM2()))
                .orElse(null));

        dto.setMeilleurCo2ParEmploye(sites.stream()
                .filter(s -> s.getCo2ParEmploye() != null)
                .min((a, b) -> Double.compare(a.getCo2ParEmploye(), b.getCo2ParEmploye()))
                .orElse(null));

        // Moyennes
        dto.setMoyenneCo2Total(sites.stream()
                .filter(s -> s.getCo2Total() != null)
                .mapToDouble(SiteResultDTO::getCo2Total)
                .average().orElse(0));

        dto.setMoyenneCo2ParM2(sites.stream()
                .filter(s -> s.getCo2ParM2() != null)
                .mapToDouble(SiteResultDTO::getCo2ParM2)
                .average().orElse(0));

        dto.setMoyenneCo2ParEmploye(sites.stream()
                .filter(s -> s.getCo2ParEmploye() != null)
                .mapToDouble(SiteResultDTO::getCo2ParEmploye)
                .average().orElse(0));

        return dto;
    }
}