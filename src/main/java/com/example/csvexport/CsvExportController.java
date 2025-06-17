package com.example.csvexport;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class CsvExportController {

    private static final String EXPORT_DIR = "exports/";

    @PostMapping("/export")
    public ResponseEntity<String> exportCSV(@RequestBody String csvData) {
        try {
            String fileName = "data_" + UUID.randomUUID() + ".csv";
            Path exportPath = Paths.get(EXPORT_DIR);
            Files.createDirectories(exportPath); // đảm bảo thư mục tồn tại
            Path filePath = exportPath.resolve(fileName);

            Files.writeString(filePath, csvData); // dùng Files.writeString thay cho FileWriter

            // Trả về URL để tải file
            return ResponseEntity.ok("/files/" + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi ghi file: " + e.getMessage());
        }
    }

    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(EXPORT_DIR).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}