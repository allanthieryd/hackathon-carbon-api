package com.hackathon.carbon.controller;

import com.hackathon.carbon.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PdfController {

    private final PdfService pdfService;

    // GET /api/pdf/site/{id} → rapport PDF d'un site
    @GetMapping("/site/{id}")
    public ResponseEntity<byte[]> exportSite(@PathVariable Long id) throws Exception {
        byte[] pdf = pdfService.genererRapportSite(id);
        return buildResponse(pdf, "rapport-site-" + id + ".pdf");
    }

    // POST /api/pdf/comparaison → rapport PDF comparaison
    // Body : [1, 2, 3]
    @PostMapping("/comparaison")
    public ResponseEntity<byte[]> exportComparaison(@RequestBody List<Long> ids) throws Exception {
        byte[] pdf = pdfService.genererRapportComparaison(ids);
        return buildResponse(pdf, "rapport-comparaison.pdf");
    }

    private ResponseEntity<byte[]> buildResponse(byte[] pdf, String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .body(pdf);
    }
}