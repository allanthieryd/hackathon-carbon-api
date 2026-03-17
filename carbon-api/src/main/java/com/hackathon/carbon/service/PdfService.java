package com.hackathon.carbon.service;

import com.hackathon.carbon.dto.SiteResultDTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final SiteService siteService;

    private static final BaseColor COLOR_GREEN = new BaseColor(46, 204, 113);
    private static final BaseColor COLOR_DARK  = new BaseColor(44, 62, 80);
    private static final BaseColor COLOR_GREY  = new BaseColor(236, 240, 241);
    private static final BaseColor COLOR_WHITE = BaseColor.WHITE;

    public byte[] genererRapportSite(Long siteId) throws Exception {
        SiteResultDTO site = siteService.getSiteById(siteId);
        return genererPdf(List.of(site), "Rapport Empreinte Carbone — " + site.getName());
    }

    public byte[] genererRapportComparaison(List<Long> ids) throws Exception {
        List<SiteResultDTO> sites = ids.stream()
                .map(siteService::getSiteById)
                .toList();
        return genererPdf(sites, "Rapport Comparaison Empreinte Carbone");
    }

    private byte[] genererPdf(List<SiteResultDTO> sites, String titre) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter.getInstance(document, baos);

        document.open();
        ajouterEnTete(document, titre);

        Font dateFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);
        document.add(new Paragraph("Généré le " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")),
                dateFont));
        document.add(Chunk.NEWLINE);

        for (SiteResultDTO site : sites) {
            ajouterSectionSite(document, site);
            document.add(Chunk.NEWLINE);
        }

        if (sites.size() > 1) {
            ajouterTableauComparaison(document, sites);
        }

        ajouterPiedDePage(document);
        document.close();
        return baos.toByteArray();
    }

    private void ajouterEnTete(Document doc, String titre) throws Exception {
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COLOR_DARK);
        cell.setPadding(20);
        cell.setBorder(Rectangle.NO_BORDER);

        Font titreFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, COLOR_WHITE);
        Font subFont   = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, COLOR_GREEN);

        Paragraph p = new Paragraph();
        p.add(new Chunk(titre + "\n", titreFont));
        p.add(new Chunk("Capgemini — Base Carbone® ADEME V23.6", subFont));
        cell.addElement(p);
        header.addCell(cell);

        doc.add(header);
        doc.add(Chunk.NEWLINE);
    }

    private void ajouterSectionSite(Document doc, SiteResultDTO site) throws Exception {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, COLOR_DARK);

        Paragraph titreSection = new Paragraph("Site : " + site.getName() + " — " + site.getLocation(), sectionFont);
        titreSection.setSpacingBefore(10);
        doc.add(titreSection);

        LineSeparator separator = new LineSeparator(1, 100, COLOR_GREEN, Element.ALIGN_LEFT, -2);
        doc.add(new Chunk(separator));
        doc.add(Chunk.NEWLINE);

        // KPIs
        PdfPTable kpiTable = new PdfPTable(3);
        kpiTable.setWidthPercentage(100);
        kpiTable.setSpacingAfter(10);
        ajouterKpiCell(kpiTable, "CO2 Total",      formatVal(site.getCo2Total()) + " kgCO2e",    COLOR_GREEN);
        ajouterKpiCell(kpiTable, "CO2 / m2",       formatVal(site.getCo2ParM2()) + " kgCO2e/m2", new BaseColor(52, 152, 219));
        ajouterKpiCell(kpiTable, "CO2 / employe",  formatVal(site.getCo2ParEmploye()) + " kgCO2e", new BaseColor(155, 89, 182));
        doc.add(kpiTable);

        // Construction / Exploitation
        PdfPTable detailTable = new PdfPTable(2);
        detailTable.setWidthPercentage(100);
        detailTable.setSpacingAfter(10);
        ajouterDetailCell(detailTable, "Construction", formatVal(site.getCo2Construction()) + " kgCO2e");
        ajouterDetailCell(detailTable, "Exploitation",  formatVal(site.getCo2Exploitation()) + " kgCO2e");
        doc.add(detailTable);

        // Infos générales
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(10);
        ajouterInfoRow(infoTable, "Superficie",           site.getSuperficie() + " m2");
        ajouterInfoRow(infoTable, "Places de parking",    String.valueOf(site.getNbParking()));
        ajouterInfoRow(infoTable, "Consommation energie", site.getConsoEnergetique() + " MWh/an");
        ajouterInfoRow(infoTable, "Nombre d'employes",    String.valueOf(site.getNbEmployes()));
        doc.add(infoTable);
    }

    private void ajouterTableauComparaison(Document doc, List<SiteResultDTO> sites) throws Exception {
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, COLOR_DARK);

        Paragraph titre = new Paragraph("Tableau comparatif", sectionFont);
        titre.setSpacingBefore(10);
        doc.add(titre);

        LineSeparator separator = new LineSeparator(1, 100, COLOR_GREEN, Element.ALIGN_LEFT, -2);
        doc.add(new Chunk(separator));
        doc.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(sites.size() + 1);
        table.setWidthPercentage(100);

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, COLOR_WHITE);
        Font cellFont   = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, COLOR_DARK);

        ajouterHeaderCell(table, "Indicateur", headerFont);
        for (SiteResultDTO site : sites) {
            ajouterHeaderCell(table, site.getName(), headerFont);
        }

        String[] indicateurs = {
                "CO2 Total (kgCO2e)",
                "CO2 par m2",
                "CO2 par employe",
                "Construction (kgCO2e)",
                "Exploitation (kgCO2e)"
        };

        boolean alternate = false;
        for (String indicateur : indicateurs) {
            BaseColor bg = alternate ? COLOR_GREY : COLOR_WHITE;
            ajouterDataCell(table, indicateur, cellFont, bg, true);

            for (SiteResultDTO site : sites) {
                String val = switch (indicateur) {
                    case "CO2 Total (kgCO2e)"    -> formatVal(site.getCo2Total());
                    case "CO2 par m2"             -> formatVal(site.getCo2ParM2());
                    case "CO2 par employe"        -> formatVal(site.getCo2ParEmploye());
                    case "Construction (kgCO2e)"  -> formatVal(site.getCo2Construction());
                    case "Exploitation (kgCO2e)"  -> formatVal(site.getCo2Exploitation());
                    default -> "-";
                };
                ajouterDataCell(table, val, cellFont, bg, false);
            }
            alternate = !alternate;
        }

        doc.add(table);
    }

    private void ajouterPiedDePage(Document doc) throws Exception {
        doc.add(Chunk.NEWLINE);
        LineSeparator separator = new LineSeparator(1, 100, COLOR_GREY, Element.ALIGN_LEFT, -2);
        doc.add(new Chunk(separator));
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
        doc.add(new Paragraph("Source : Base Carbone ADEME V23.6 — Application Empreinte Carbone Capgemini", footerFont));
    }

    private void ajouterKpiCell(PdfPTable table, String label, String value, BaseColor color) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new BaseColor(240, 249, 244));
        cell.setPadding(12);
        cell.setBorderColor(color);
        cell.setBorderWidth(2);

        Font labelFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, color);

        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", labelFont));
        p.add(new Chunk(value, valueFont));
        cell.addElement(p);
        table.addCell(cell);
    }

    private void ajouterDetailCell(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, COLOR_DARK);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COLOR_GREY);
        cell.setPadding(10);
        cell.setBorder(Rectangle.NO_BORDER);

        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", labelFont));
        p.add(new Chunk(value, valueFont));
        cell.addElement(p);
        table.addCell(cell);
    }

    private void ajouterInfoRow(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, COLOR_DARK);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.DARK_GRAY);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(6);
        labelCell.setBorderColor(COLOR_GREY);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(6);
        valueCell.setBorderColor(COLOR_GREY);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void ajouterHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(COLOR_DARK);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void ajouterDataCell(PdfPTable table, String text, Font font, BaseColor bg, boolean bold) {
        Font f = bold ? new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, COLOR_DARK) : font;
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setHorizontalAlignment(bold ? Element.ALIGN_LEFT : Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private String formatVal(Double val) {
        if (val == null) return "-";
        return String.format("%,.0f", val);
    }
}