package org.springshop.storage_service.repository;


import org.springshop.storage_service.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByFilename(String filename);
    void deleteByFilename(String filename);
}
