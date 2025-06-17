package com.example.csvexport;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class CsvExportController {

    private static final String EXPORT_DIR = "exports/";

    @PostMapping("/export")
    public ResponseEntity<String> exportCSV(@RequestBody String csvData) {
        try {
            String fileName = "data_" + UUID.randomUUID() + ".csv";
            java.nio.file.Path exportPath = Paths.get(EXPORT_DIR);
            java.nio.file.Files.createDirectories(exportPath);
            String filePath = EXPORT_DIR + fileName;
            FileWriter writer = new FileWriter(filePath);
            writer.write(csvData);
            writer.close();

            return ResponseEntity.ok("/files/" + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Lỗi ghi file: " + e.getMessage());
        }
    }
}