package org.springshop.storage_service.controller;

import org.springshop.storage_service.model.File;
import org.springshop.storage_service.service.FileStorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileStorageService storageService;

    public FileController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<File> upload(@RequestParam("file") MultipartFile file) throws IOException {
        File saved = storageService.saveFile(file);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String filename) throws IOException {
        byte[] data = storageService.getFile(filename);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(resource);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> delete(@PathVariable String filename) throws IOException {
        storageService.deleteFile(filename);
        return ResponseEntity.noContent().build();
    }
}

