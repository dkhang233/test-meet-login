package com.dkhang.testmeetevent.controllers;

import com.dkhang.testmeetevent.dtos.chat.ChatNotification;
import com.dkhang.testmeetevent.models.ChatMessage;
import com.dkhang.testmeetevent.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate; // 1 opject giúp ta có thể gửi 1 đối tượng hoặc tin nhắn vào hàng đợi
    private final ChatMessageService chatMessageService;

    //thông báo tin nhắn
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage){
        ChatMessage saveMessage = chatMessageService.save(chatMessage);
        System.out.println("save chat message");
        simpMessagingTemplate.convertAndSendToUser(  //chuyển đổi và gửi đến hàng đợi người dùng
                chatMessage.getRecipientId(),       //người nhận
                "/queue/messages",                   //điểm đến
                ChatNotification.builder()          //object payload
                        .id(saveMessage.getId())
                        .senderId(saveMessage.getSenderId())
                        .recipientId(saveMessage.getRecipientId())
                        .content(saveMessage.getContent())
                        .build()
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(
            @PathVariable(value = "senderId") String senderId,
            @PathVariable(value = "recipientId") String recipientId
    ){
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId,recipientId));
    }
}
