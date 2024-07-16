package com.practicas.practicaviiiiso818appb.service;

import com.practicas.practicaviiiiso818appb.model.PdfFile;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Log4j2
public class SearchFileService implements SearchFile{

    @Override
    public List<PdfFile> findFileByUserIdentification(String identification) {
        String tempDir = System.getProperty("java.io.tmpdir");
        File dir = new File(tempDir);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".pdf"));

        List<PdfFile> userFiles = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.getName().contains(identification)) {
                    userFiles.add(new PdfFile(file.getName(), file.getAbsolutePath()));
                }
            }
        }
        return userFiles;
    }
}
