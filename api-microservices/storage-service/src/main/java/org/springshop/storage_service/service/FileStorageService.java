package org.springshop.storage_service.service;

import org.springframework.stereotype.Service;

import org.springshop.storage_service.model.File;
import org.springshop.storage_service.repository.FileRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FileStorageService {

    private final Path rootDir;
    private final FileRepository fileRepository;

    public FileStorageService(
            @Value("${storage.location:data/uploads}") String storagePath,
            FileRepository fileRepository) throws IOException {
        this.rootDir = Paths.get(storagePath);
        this.fileRepository = fileRepository;
        Files.createDirectories(rootDir);
    }

    public File saveFile(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = rootDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        File entity = new File();
        entity.setFilename(filename);
        entity.setOriginalName(file.getOriginalFilename());
        entity.setContentType(file.getContentType());
        entity.setSize(file.getSize());
        entity.setPath(filePath.toString());

        return fileRepository.save(entity);
    }

    public byte[] getFile(String filename) throws IOException {
        Path path = rootDir.resolve(filename);
        return Files.readAllBytes(path);
    }

    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }

    public void deleteFile(String filename) throws IOException {
        File file = fileRepository.findByFilename(filename)
                .orElseThrow(() -> new RuntimeException("File not found"));
        Files.deleteIfExists(Paths.get(file.getPath()));
        fileRepository.deleteByFilename(filename);
    }
}

