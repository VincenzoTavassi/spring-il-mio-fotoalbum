package com.project.fotoalbum.api;

import com.project.fotoalbum.models.UserMessage;
import com.project.fotoalbum.repository.UserMessageRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/messages")
public class UserMessageRestController {

    @Autowired
    UserMessageRepository userMessageRepository;



    //
//    @GetMapping
//    public List<UserMessage> index() {
//        return userMessageRepository.findAll();
//    }

//    @GetMapping("/{id}")
//    public UserMessage getById(@PathVariable Integer id) {
//        Optional<UserMessage> foundMessage = userMessageRepository.findById(id);
//        if (foundMessage.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        else return foundMessage.get();
//    }

    @PostMapping("/create")
    public UserMessage create(@Valid @RequestBody UserMessage userMessage) {
        try {
            userMessage.setCreatedAt(LocalDateTime.now());
            userMessage.setRead(false);
            return userMessageRepository.save(userMessage);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/read/{id}")
        public UserMessage read(@PathVariable Integer id) {
        Optional<UserMessage> foundMessage = userMessageRepository.findById(id);
        if (foundMessage.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        else {
            foundMessage.get().setRead(!foundMessage.get().isRead());
            return userMessageRepository.save(foundMessage.get());
        }
    }

}
