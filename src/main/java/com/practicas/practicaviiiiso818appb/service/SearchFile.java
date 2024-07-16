package com.practicas.practicaviiiiso818appb.service;

import com.practicas.practicaviiiiso818appb.model.PdfFile;

import java.io.File;
import java.util.List;

public interface SearchFile {
    List<PdfFile> findFileByUserIdentification(String identification);
}
