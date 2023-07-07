package com.project.fotoalbum.controllers;

import com.project.fotoalbum.models.UserMessage;
import com.project.fotoalbum.service.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/messages")
public class UserMessageController {

    @Autowired
    UserMessageService userMessageService;

    @GetMapping
    public String index(Model model) {
        List<UserMessage> messages = userMessageService.getAll();
        model.addAttribute("messages", messages);
        return "messages/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Integer id, Model model) {
        UserMessage message = userMessageService.getById(id);
        model.addAttribute("message", message);
        return "messages/show";
    }

    @PostMapping("/read/{id}")
    public String read(@RequestParam("read") boolean read, @PathVariable Integer id) {
        UserMessage message = userMessageService.getById(id);
        message.setRead(read);
        userMessageService.update(message);
        return "redirect:/messages";
    }

}
