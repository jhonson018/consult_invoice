package com.practicas.practicaviiiiso818appb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class ToImageService {
    public BufferedImage convertPdfToImage(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            log.info("Received pdf name {}", document.getDocument().toString());
            // Renderizar la primera página del PDF
            return pdfRenderer.renderImageWithDPI(0, 300);
        }
    }
}
