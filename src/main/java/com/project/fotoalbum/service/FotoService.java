package com.project.fotoalbum.service;

import com.project.fotoalbum.models.Foto;
import com.project.fotoalbum.repository.FotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Service
public class FotoService {

@Autowired
    FotoRepository fotoRepository;

// Ottieni tutte le foto con query param opzionale
    public List<Foto> getAll(boolean visible, Optional<String> keyword) {
        List<Foto> fotoList = null;
        if (visible) { // Se la richiesta è per le foto visibili
            if (keyword.isPresent()) // Se c'è una keyword filtro per la keyword fornita
                fotoList = fotoRepository.findByTitleContainingIgnoreCaseAndVisibleTrue(keyword.get());
            if (keyword.isEmpty())
                fotoList = fotoRepository.findByVisibleTrue(); // Altrimenti invio tutte le foto visibili
        } else { // Se la richiesta è per visible false
            if (keyword.isPresent()) // Se c'è una keyword filtro per quella
                fotoList = fotoRepository.findByTitleContainingIgnoreCase(keyword.get());
            if (keyword.isEmpty()) // Altrimenti invio tutte le foto
                fotoList = fotoRepository.findAll();
        }
        return fotoList;
    }

}
