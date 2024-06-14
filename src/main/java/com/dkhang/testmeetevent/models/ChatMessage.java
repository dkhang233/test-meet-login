package com.dkhang.testmeetevent.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String chatId;
    private String SenderId;
    private String recipientId;
    private String content;
    private Date timestamp;
}
