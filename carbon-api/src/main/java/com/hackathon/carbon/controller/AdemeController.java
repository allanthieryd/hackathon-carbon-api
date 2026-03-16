package com.hackathon.carbon.controller;

import com.hackathon.carbon.dto.FacteurEmissionDTO;
import com.hackathon.carbon.service.AdemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ademe")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdemeController {

    private final AdemeService ademeService;

    // GET /api/ademe/facteurs → tous les facteurs d'émission
    @GetMapping("/facteurs")
    public ResponseEntity<List<FacteurEmissionDTO>> getFacteurs() {
        return ResponseEntity.ok(ademeService.getFacteursEmission());
    }

    // GET /api/ademe/facteurs/defaut → valeurs officielles ADEME V23.6 sans appel API
    @GetMapping("/facteurs/defaut")
    public ResponseEntity<List<FacteurEmissionDTO>> getFacteursDefaut() {
        return ResponseEntity.ok(ademeService.getFacteursDefaut());
    }
}