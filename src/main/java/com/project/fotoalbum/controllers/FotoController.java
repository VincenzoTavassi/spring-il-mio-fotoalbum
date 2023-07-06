package com.project.fotoalbum.controllers;

import com.project.fotoalbum.exceptions.FotoNotFoundException;
import com.project.fotoalbum.messages.Message;
import com.project.fotoalbum.messages.MessageType;
import com.project.fotoalbum.models.Category;
import com.project.fotoalbum.models.Foto;
import com.project.fotoalbum.repository.CategoryRepository;
import com.project.fotoalbum.service.FotoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class FotoController {

    @Autowired
    FotoService fotoService;

    @Autowired
    CategoryRepository categoryRepository;


    @GetMapping
    public String index() {
        return "redirect:/foto";
    }
    @GetMapping("/foto")
    public String index(@RequestParam Optional<String> keyword, Model model) {
        List<Foto> fotoList = null;
        if (keyword.isPresent()) fotoList = fotoService.getAll(false, keyword);
        else fotoList = fotoService.getAll(false, Optional.empty());
        model.addAttribute("fotoList", fotoList);
        return "foto/index";
    }

    @GetMapping("/foto/{id}")
    public String show(@PathVariable Integer id, Model model) {
        Foto foto = fotoService.getById(id);
        model.addAttribute("foto", foto);
        return "foto/show";
    }

    @GetMapping("foto/create")
    public String create(Model model) {
        Foto foto = new Foto();
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("foto", foto);
        model.addAttribute("categories", categories);
        return "foto/form";
    }

    @PostMapping("foto/create")
    public String create(
            @Valid @ModelAttribute("foto") Foto foto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "foto/form";
        }
        try {
            fotoService.create(foto);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        redirectAttributes.addFlashAttribute("message", new Message(MessageType.SUCCESS, "Foto creata con successo."));
        return "redirect:/foto";
    }

    @GetMapping("foto/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        try {
            Foto fotoToUpdate = fotoService.getById(id);
            model.addAttribute("foto", fotoToUpdate);
            model.addAttribute("categories", categoryRepository.findAll());
        } catch (FotoNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        return "foto/form";
    }

    @PostMapping("foto/edit")
    public String update(
            @Valid @ModelAttribute("foto") Foto foto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
            ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "foto/form";
        }
        try {
            fotoService.edit(foto);
        } catch (FotoNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        redirectAttributes.addFlashAttribute("message", new Message(MessageType.SUCCESS, "Foto modificata con successo."));
        return "redirect:/foto";
    }

}
