package com.hackathon.carbon.service;

import com.hackathon.carbon.dto.SiteDTO;
import com.hackathon.carbon.dto.SiteResultDTO;
import com.hackathon.carbon.model.Site;
import com.hackathon.carbon.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    // ── Facteurs d'émission ADEME (kgCO₂e / tonne de matériau)
    private static final double FACTEUR_BETON = 250.0;
    private static final double FACTEUR_ACIER = 1800.0;
    private static final double FACTEUR_VERRE = 900.0;
    private static final double FACTEUR_BOIS  = 50.0;

    // kgCO₂e par MWh (réseau électrique français, ADEME 2024)
    private static final double FACTEUR_ENERGIE = 57.0;

    // ── Calculs ────────────────────────────────────────────────

    public double calculerConstruction(Site site) {
        double beton = site.getQuantiteBeton() != null ? site.getQuantiteBeton() * FACTEUR_BETON : 0;
        double acier = site.getQuantiteAcier() != null ? site.getQuantiteAcier() * FACTEUR_ACIER : 0;
        double verre = site.getQuantiteVerre() != null ? site.getQuantiteVerre() * FACTEUR_VERRE : 0;
        double bois  = site.getQuantiteBois()  != null ? site.getQuantiteBois()  * FACTEUR_BOIS  : 0;
        return beton + acier + verre + bois;
    }

    public double calculerExploitation(Site site) {
        if (site.getConsoEnergetique() == null) return 0;
        // MWh → kWh (*1000) puis × facteur
        return site.getConsoEnergetique() * 1000 * FACTEUR_ENERGIE;
    }

    // ── CRUD ───────────────────────────────────────────────────

    public SiteResultDTO createSite(SiteDTO dto) {
        Site site = new Site();
        site.setName(dto.getName());
        site.setLocation(dto.getLocation());
        site.setSuperficie(dto.getSuperficie());
        site.setNbParking(dto.getNbParking());
        site.setConsoEnergetique(dto.getConsoEnergetique());
        site.setNbEmployes(dto.getNbEmployes());
        site.setQuantiteBeton(dto.getQuantiteBeton());
        site.setQuantiteAcier(dto.getQuantiteAcier());
        site.setQuantiteVerre(dto.getQuantiteVerre());
        site.setQuantiteBois(dto.getQuantiteBois());
        site.setCreatedAt(LocalDateTime.now());
        site.setUpdatedAt(LocalDateTime.now());

        double co2Construction = calculerConstruction(site);
        double co2Exploitation = calculerExploitation(site);
        double co2Total        = co2Construction + co2Exploitation;

        site.setCo2Construction(co2Construction);
        site.setCo2Exploitation(co2Exploitation);
        site.setCo2Total(co2Total);

        siteRepository.save(site);
        return toResultDTO(site);
    }

    public List<SiteResultDTO> getAllSites() {
        return siteRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResultDTO)
                .collect(Collectors.toList());
    }

    public SiteResultDTO getSiteById(Long id) {
        Site site = siteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Site not found: " + id));
        return toResultDTO(site);
    }

    public void deleteSite(Long id) {
        siteRepository.deleteById(id);
    }

    // ── Mapper ─────────────────────────────────────────────────

    private SiteResultDTO toResultDTO(Site site) {
        SiteResultDTO dto = new SiteResultDTO();
        dto.setId(site.getId());
        dto.setName(site.getName());
        dto.setLocation(site.getLocation());
        dto.setSuperficie(site.getSuperficie());
        dto.setNbParking(site.getNbParking());
        dto.setConsoEnergetique(site.getConsoEnergetique());
        dto.setNbEmployes(site.getNbEmployes());
        dto.setQuantiteBeton(site.getQuantiteBeton());
        dto.setQuantiteAcier(site.getQuantiteAcier());
        dto.setQuantiteVerre(site.getQuantiteVerre());
        dto.setQuantiteBois(site.getQuantiteBois());
        dto.setCo2Construction(site.getCo2Construction());
        dto.setCo2Exploitation(site.getCo2Exploitation());
        dto.setCo2Total(site.getCo2Total());

        // KPIs dérivés
        if (site.getSuperficie() != null && site.getSuperficie() > 0)
            dto.setCo2ParM2(site.getCo2Total() / site.getSuperficie());

        if (site.getNbEmployes() != null && site.getNbEmployes() > 0)
            dto.setCo2ParEmploye(site.getCo2Total() / site.getNbEmployes());

        return dto;
    }
}