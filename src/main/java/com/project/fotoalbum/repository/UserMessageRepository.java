package com.project.fotoalbum.repository;

import com.project.fotoalbum.models.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMessageRepository extends JpaRepository <UserMessage, Integer> {
}
