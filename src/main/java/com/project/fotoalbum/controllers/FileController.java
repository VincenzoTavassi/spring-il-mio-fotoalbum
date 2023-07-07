package com.project.fotoalbum.controllers;

import com.project.fotoalbum.exceptions.FotoNotFoundException;
import com.project.fotoalbum.models.Foto;
import com.project.fotoalbum.service.FotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/files")
public class FileController {

    @Autowired
    FotoService fotoService;
    @GetMapping("/{id}")

    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
        try {
            Foto foundFoto = fotoService.getById(id);
            MediaType mediaType = MediaType.IMAGE_JPEG;
            return ResponseEntity.ok().contentType(mediaType).body(foundFoto.getDBimage());
        } catch (FotoNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
