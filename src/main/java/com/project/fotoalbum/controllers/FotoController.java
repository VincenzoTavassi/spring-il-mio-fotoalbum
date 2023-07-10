package com.project.fotoalbum.controllers;

import com.project.fotoalbum.dto.FotoForm;
import com.project.fotoalbum.exceptions.FotoIsRequiredException;
import com.project.fotoalbum.exceptions.FotoNotFoundException;
import com.project.fotoalbum.messages.Message;
import com.project.fotoalbum.messages.MessageType;
import com.project.fotoalbum.models.Category;
import com.project.fotoalbum.models.Foto;
import com.project.fotoalbum.repository.CategoryRepository;
import com.project.fotoalbum.service.FotoService;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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
    public String index(
            @RequestParam Optional<String> keyword,
            Model model,
            Authentication authentication) {
        String authUser = authentication.getName();
        List<Foto> fotoList = null;
        if (keyword.isPresent()) fotoList = fotoService.getByActiveUser(authUser, keyword);
        else fotoList = fotoService.getByActiveUser(authUser, Optional.empty());
        model.addAttribute("fotoList", fotoList);
        model.addAttribute("keyword", keyword.orElse(""));
        return "foto/index";
    }

    @GetMapping("/foto/{id}")
    public String show(@PathVariable Integer id, Model model, Authentication authentication) {
        Foto foto = null;
        try {
            foto = fotoService.getByIdWithAuth(id, authentication.getName());
        } catch (AuthException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
        model.addAttribute("foto", foto);
        return "foto/show";
    }

    @GetMapping("foto/create")
    public String create(Model model, Authentication authentication) {
        FotoForm foto = new FotoForm();
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("foto", foto);
        model.addAttribute("categories", categories);
        model.addAttribute("user", authentication.getName());
        return "foto/form";
    }

    @PostMapping("foto/create")
    public String create(
            @Valid @ModelAttribute("foto") FotoForm foto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            Authentication authentication
    ) {
        try {
            fotoService.create(foto, authentication.getName());
        } catch (FotoIsRequiredException e) {
            bindingResult.addError(new FieldError("foto", "pictureUrl", foto.getPictureUrl(), false, null, null,
                    e.getMessage()));
        }
        if(bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "foto/form";
        }
        redirectAttributes.addFlashAttribute("message", new Message(MessageType.SUCCESS, "Foto creata con successo."));
        return "redirect:/foto";
    }

    @GetMapping("foto/edit/{id}")
    public String edit(@PathVariable Integer id, Model model, Authentication authentication) {
        try {
            Foto foto = fotoService.getByIdWithAuth(id, authentication.getName());
            FotoForm fotoToUpdate = fotoService.fromFotoToFotoForm(foto);
            model.addAttribute("foto", fotoToUpdate);
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("user", authentication.getName());
        } catch (FotoNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch(AuthException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
        return "foto/form";
    }

    @PostMapping("foto/edit")
    public String update(
            @Valid @ModelAttribute("foto") FotoForm foto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model,
            Authentication authentication
            ) {
        try {
            fotoService.edit(foto, authentication.getName());
        } catch (AuthException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (FotoNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (FotoIsRequiredException e) {
            bindingResult.addError(new FieldError("foto", "pictureUrl", foto.getPictureUrl(), false, null, null,
                    e.getMessage()));
        }
            if (bindingResult.hasErrors()) {
                model.addAttribute("categories", categoryRepository.findAll());
                return "foto/form";
            }
        redirectAttributes.addFlashAttribute("message", new Message(MessageType.SUCCESS, "Foto modificata con successo."));
        return "redirect:/foto";
    }

    @PostMapping("foto/delete/{id}")
    public String delete(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes,
            Authentication authentication
    ) {
       try {
           fotoService.delete(id, authentication.getName());
       } catch (FotoNotFoundException e) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
       } catch (AuthException e) {
           throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
       }
        redirectAttributes.addFlashAttribute("message", new Message(MessageType.SUCCESS, "Foto eliminata con successo."));
       return "redirect:/foto";
    }

}
