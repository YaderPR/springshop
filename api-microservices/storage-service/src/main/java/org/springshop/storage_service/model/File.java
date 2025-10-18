package org.springshop.storage_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter @Getter
@Entity
@Table(name = "file")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String originalName;
    private String contentType;
    private long size;
    private String path;

    @Column(updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
}

