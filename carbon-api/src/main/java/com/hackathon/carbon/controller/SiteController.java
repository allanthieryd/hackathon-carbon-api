package com.hackathon.carbon.controller;

import com.hackathon.carbon.dto.SiteDTO;
import com.hackathon.carbon.dto.SiteResultDTO;
import com.hackathon.carbon.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permet les appels depuis Angular et React Native
public class SiteController {

    private final SiteService siteService;

    // GET /api/sites → liste tous les sites
    @GetMapping
    public ResponseEntity<List<SiteResultDTO>> getAllSites() {
        return ResponseEntity.ok(siteService.getAllSites());
    }

    // GET /api/sites/{id} → un site par id
    @GetMapping("/{id}")
    public ResponseEntity<SiteResultDTO> getSiteById(@PathVariable Long id) {
        return ResponseEntity.ok(siteService.getSiteById(id));
    }

    // POST /api/sites → créer un site + calculer CO₂
    @PostMapping
    public ResponseEntity<SiteResultDTO> createSite(@RequestBody SiteDTO dto) {
        return ResponseEntity.ok(siteService.createSite(dto));
    }

    // DELETE /api/sites/{id} → supprimer un site
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        siteService.deleteSite(id);
        return ResponseEntity.noContent().build();
    }
}