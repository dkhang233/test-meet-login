package com.dkhang.testmeetevent.models;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "chat_room")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String chatId;
    private String senderId;
    private String recipientId;
}
