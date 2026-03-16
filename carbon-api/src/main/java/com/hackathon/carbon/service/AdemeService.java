package com.hackathon.carbon.service;

import com.hackathon.carbon.dto.FacteurEmissionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AdemeService {

    private static final String ADEME_API_URL =
            "https://data.ademe.fr/data-fair/api/v1/datasets/base-carboner/lines";

    // Facteurs officiels ADEME Base Carbone V23.6 (fallback si API indisponible)
    private static final Map<String, Double> FACTEURS_DEFAUT = Map.of(
            "Béton",    250.0,   // kgCO2e/tonne
            "Acier",    1800.0,  // kgCO2e/tonne
            "Verre",    900.0,   // kgCO2e/tonne
            "Bois",     50.0,    // kgCO2e/tonne
            "Électricité France", 57.0  // kgCO2e/MWh
    );

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Tente de récupérer les facteurs depuis l'API ADEME.
     * Si l'API est indisponible, retourne les valeurs de fallback officielles.
     */
    public List<FacteurEmissionDTO> getFacteursEmission() {
        try {
            return fetchFromAdemeApi();
        } catch (Exception e) {
            log.warn("API ADEME indisponible, utilisation des valeurs officielles V23.6 : {}", e.getMessage());
            return getFacteursDefaut();
        }
    }

    /**
     * Appel réel à l'API ADEME data.ademe.fr
     */
    @SuppressWarnings("unchecked")
    private List<FacteurEmissionDTO> fetchFromAdemeApi() {
        List<FacteurEmissionDTO> facteurs = new ArrayList<>();

        String[] materiaux = {"béton", "acier", "verre", "bois", "électricité"};

        for (String materiau : materiaux) {
            String url = UriComponentsBuilder.fromHttpUrl(ADEME_API_URL)
                    .queryParam("q", materiau)
                    .queryParam("size", "1")
                    .queryParam("select", "Nom_base_français,Total_poste_non_décomposé,Unité_français")
                    .queryParam("qs", "Statut_de_l_élément:Validé")
                    .build()
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                if (!results.isEmpty()) {
                    Map<String, Object> item = results.get(0);
                    FacteurEmissionDTO dto = new FacteurEmissionDTO();
                    dto.setMateriau(String.valueOf(item.getOrDefault("Nom_base_français", materiau)));
                    Object valeur = item.get("Total_poste_non_décomposé");
                    dto.setFacteur(valeur != null ? Double.parseDouble(String.valueOf(valeur)) : FACTEURS_DEFAUT.getOrDefault(materiau, 0.0));
                    dto.setUnite(String.valueOf(item.getOrDefault("Unité_français", "kgCO2e/tonne")));
                    dto.setSource("API Base Carbone® ADEME");
                    dto.setVersion("V23.6");
                    facteurs.add(dto);
                }
            }
        }

        return facteurs.isEmpty() ? getFacteursDefaut() : facteurs;
    }

    /**
     * Valeurs officielles ADEME Base Carbone V23.6 (fallback)
     */
    public List<FacteurEmissionDTO> getFacteursDefaut() {
        List<FacteurEmissionDTO> facteurs = new ArrayList<>();

        facteurs.add(creerFacteur("Béton",               250.0,  "kgCO2e/tonne"));
        facteurs.add(creerFacteur("Acier",               1800.0, "kgCO2e/tonne"));
        facteurs.add(creerFacteur("Verre",               900.0,  "kgCO2e/tonne"));
        facteurs.add(creerFacteur("Bois",                50.0,   "kgCO2e/tonne"));
        facteurs.add(creerFacteur("Électricité France",  57.0,   "kgCO2e/MWh"));
        facteurs.add(creerFacteur("Parking béton",       656.0,  "kgCO2e/m²"));
        facteurs.add(creerFacteur("Parking métal",       220.0,  "kgCO2e/m²"));

        return facteurs;
    }

    private FacteurEmissionDTO creerFacteur(String materiau, Double facteur, String unite) {
        FacteurEmissionDTO dto = new FacteurEmissionDTO();
        dto.setMateriau(materiau);
        dto.setFacteur(facteur);
        dto.setUnite(unite);
        dto.setSource("Base Carbone® ADEME");
        dto.setVersion("V23.6");
        return dto;
    }

    /**
     * Retourne le facteur d'un matériau spécifique (utilisé par SiteService)
     */
    public Double getFacteurByMateriau(String materiau) {
        return FACTEURS_DEFAUT.getOrDefault(materiau, 0.0);
    }
}