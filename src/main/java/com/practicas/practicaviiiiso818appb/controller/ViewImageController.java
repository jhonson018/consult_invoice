package com.practicas.practicaviiiiso818appb.controller;

import com.practicas.practicaviiiiso818appb.model.PdfFile;
import com.practicas.practicaviiiiso818appb.service.SearchFileService;
import com.practicas.practicaviiiiso818appb.service.ToImageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@Log4j2
public class ViewImageController {

    @Autowired
    private SearchFileService pdfSearchService;

    @Autowired
    private ToImageService pdfToImageService;

    @GetMapping("/pdf/display")
    public String displayPdf(@RequestParam("fileName") String fileName, Model model) {
        String tempDir = System.getProperty("java.io.tmpdir");
        File pdfFile = new File(tempDir, fileName);

        if (pdfFile.exists()) {
            try {
                BufferedImage image = pdfToImageService.convertPdfToImage(pdfFile);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                byte[] imageBytes = baos.toByteArray();

                String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
                model.addAttribute("base64Image", base64Image);
                return "pdfDisplay"; // Nombre del template Thymeleaf

            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "Error processing PDF file.");
                return "error"; // Nombre del template de error
            }
        } else {
            model.addAttribute("error", "PDF file not found: " + fileName);
            return "error"; // Nombre del template de error
        }
    }

    @GetMapping("/pdf/search")
    public String searchForm() {
        return "searchForm"; // Nombre del template Thymeleaf para el formulario de búsqueda
    }

    @GetMapping("/pdf/list")
    public String listPdfs(@RequestParam("userId") String userId, Model model) {
        List<PdfFile> pdfFiles = pdfSearchService.findFileByUserIdentification(userId);
        log.info("Enviando pdfFiles: {}", pdfFiles.toString());
        if (!pdfFiles.isEmpty()) {
            model.addAttribute("pdfFiles", pdfFiles);
            model.addAttribute("userId", userId);
            return "pdfList"; // Nombre del template Thymeleaf para listar los PDFs
        } else {
            model.addAttribute("error", "No PDF files found for user ID: " + userId);
            return "error"; // Nombre del template de error
        }
    }

    @GetMapping("/pdf/displayThumbnail")
    public ResponseEntity<InputStreamResource> displayThumbnail(@RequestParam("fileName") String fileName) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File pdfFile = new File(tempDir, fileName);

        if (pdfFile.exists()) {
            BufferedImage image = pdfToImageService.convertPdfToImage(pdfFile);

            // Crear una miniatura
            BufferedImage thumbnail = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            thumbnail.createGraphics().drawImage(image, 0, 0, 100, 100, null);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "png", baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(new InputStreamResource(bais));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
