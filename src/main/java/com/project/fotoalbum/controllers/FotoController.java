package com.project.fotoalbum.controllers;

import com.project.fotoalbum.models.Foto;
import com.project.fotoalbum.service.FotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/foto")
public class FotoController {

    @Autowired
    FotoService fotoService;
    @GetMapping
    public String index(@RequestParam Optional<String> keyword, Model model) {
        List<Foto> fotoList = null;
        if (keyword.isPresent()) fotoList = fotoService.getAll(false, keyword);
        else fotoList = fotoService.getAll(false, Optional.empty());
        model.addAttribute("fotoList", fotoList);
        return "foto/index";
    }

}
