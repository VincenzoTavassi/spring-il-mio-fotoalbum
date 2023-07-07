package com.project.fotoalbum.api;

import com.project.fotoalbum.exceptions.FotoNotFoundException;
import com.project.fotoalbum.models.Foto;
import com.project.fotoalbum.service.FotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("api/v1/foto")
public class FotoRestController {

    @Autowired
    FotoService fotoService;
    @GetMapping
    public List<Foto> index(@RequestParam Optional<String> keyword) {
        return fotoService.getAll(true, keyword);
    }

    @GetMapping("/{id}")
    public Foto show(@PathVariable Integer id) {
        try {
            return fotoService.getById(id);
        } catch (FotoNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
