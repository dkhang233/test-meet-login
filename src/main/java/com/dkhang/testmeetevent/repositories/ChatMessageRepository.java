package com.dkhang.testmeetevent.repositories;

import com.dkhang.testmeetevent.models.ChatMessage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,String> {
    List<ChatMessage> findByChatId(String chatId);
}
