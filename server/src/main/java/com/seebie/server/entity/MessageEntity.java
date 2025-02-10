package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "message")
public class MessageEntity implements Serializable {

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

}
