package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.ai.chat.messages.Message;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "message")
public class MessageEntity implements Serializable, Message {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private String text = "";

    @Basic
    @NotNull
    private Instant time;

    @Column(name = "type_id")
    @Enumerated(EnumType.ORDINAL)
    private MessageType type;

    protected MessageEntity() {
        // no arg constructor is required by JPA
    }

    public MessageEntity(User user, String text, MessageType type) {
        this.user = user;
        this.text = text;
        this.type = type;
        this.time = Instant.now();
    }

    public String getText() {
        return text;
    }

    @Deprecated
    @Override
    public String getContent() {
        return text;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of();
    }

    @Override
    public org.springframework.ai.chat.messages.MessageType getMessageType() {
        return switch (type) {
            case USER -> org.springframework.ai.chat.messages.MessageType.USER;
            case ASSISTANT -> org.springframework.ai.chat.messages.MessageType.ASSISTANT;
        };
    }
}
