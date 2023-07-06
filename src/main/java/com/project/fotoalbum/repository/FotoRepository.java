package com.project.fotoalbum.repository;

import com.project.fotoalbum.models.Foto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FotoRepository extends JpaRepository<Foto, Integer> {

    List<Foto> findByTitleContainingIgnoreCase(String searchTerm);
    List<Foto> findByTitleContainingIgnoreCaseAndVisibleTrue(String searchTerm);
    List<Foto> findByTitleContainingIgnoreCaseAndVisibleFalse(String searchTerm);
    List<Foto> findByVisibleTrue();
    List<Foto> findByVisibleFalse();
}
