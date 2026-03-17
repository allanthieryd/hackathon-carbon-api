package com.hackathon.carbon.controller;

import com.hackathon.carbon.dto.ComparaisonDTO;
import com.hackathon.carbon.service.ComparaisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comparaison")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComparaisonController {

    private final ComparaisonService comparaisonService;

    // POST /api/comparaison → comparer une liste de sites
    // Body : [1, 2, 3]
    @PostMapping
    public ResponseEntity<ComparaisonDTO> comparerSites(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(comparaisonService.comparerSites(ids));
    }
}