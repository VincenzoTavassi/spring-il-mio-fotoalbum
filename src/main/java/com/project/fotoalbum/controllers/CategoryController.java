package com.project.fotoalbum.controllers;

import com.project.fotoalbum.messages.Message;
import com.project.fotoalbum.messages.MessageType;
import com.project.fotoalbum.models.Category;
import com.project.fotoalbum.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping
    public String index(Model model) {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("category", new Category());
        return "categories/index";

    }

    @PostMapping("/add")
    public String create(
            @Valid @ModelAttribute("category") Category category,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        Optional<Category> foundCategory = categoryRepository.findByName(category.getName());
        if (foundCategory.isPresent()) bindingResult.addError(new FieldError("category", "name", category.getName(), false, null, null,
                "Il nome deve essere unico"));
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "categories/index";
        }
        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("message", new Message(MessageType.SUCCESS, "Categoria aggiunta con successo."));
        return "redirect:/categories";
    }

    @PostMapping("/edit")
    public String update(
            @RequestParam("categoryId") Integer id,
            @Valid @ModelAttribute("category") Category category,
            Model model,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        Optional<Category> foundCategory = categoryRepository.findById(id);
        if (foundCategory.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if(!isUniqueCategory(category) && category.getName().equalsIgnoreCase(foundCategory.get().getName())) {
            bindingResult.addError((new FieldError("category", "name", category.getName(), false, null, null,
                    "Il nome deve essere unico")));
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "categories/index";}
        foundCategory.get().setName(category.getName());
        categoryRepository.save(foundCategory.get());
        redirectAttributes.addFlashAttribute("message", new Message(MessageType.SUCCESS, "Categoria modificata con successo."));
        return "redirect:/categories";
    }

    @PostMapping("/delete")
    public String delete(
            @RequestParam("categoryId") Integer id,
            RedirectAttributes redirectAttributes) {
        Optional<Category> foundCategory = categoryRepository.findById(id);
        if (foundCategory.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (foundCategory.get().getFotos().size() > 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria piena");
        else categoryRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", new Message(MessageType.SUCCESS, "Categoria eliminata con successo."));
        return "redirect:/categories";

    }

    private boolean isUniqueCategory(Category category) {
        Optional<Category> categoryToCheck = categoryRepository.findByName(category.getName());
        return categoryToCheck.isEmpty();
    }
}
