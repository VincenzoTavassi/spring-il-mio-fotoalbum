package com.project.fotoalbum.service;

import com.project.fotoalbum.exceptions.MessageNotFoundException;
import com.project.fotoalbum.models.UserMessage;
import com.project.fotoalbum.repository.UserMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserMessageService {

    @Autowired
    UserMessageRepository userMessageRepository;

    public List<UserMessage> getAll() {
        return userMessageRepository.findAll();
    }

    public UserMessage getById(Integer id) {
        Optional<UserMessage> foundMessage = userMessageRepository.findById(id);
        if(foundMessage.isEmpty()) throw new MessageNotFoundException("Messaggio non trovato");
        foundMessage.get().setRead(true);
        return userMessageRepository.save(foundMessage.get());
    }

    public void update(UserMessage userMessage) {
        Optional<UserMessage> DBmessage = userMessageRepository.findById(userMessage.getId());
        if (DBmessage.isEmpty()) throw new MessageNotFoundException("Messaggio non trovato");
        DBmessage.get().setRead(userMessage.isRead());
        userMessageRepository.save(DBmessage.get());
    }
}
