package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "message")
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private String content = "";

    @Basic
    @NotNull
    private Instant time;

    @Column(name = "type_id")
    @Enumerated(EnumType.ORDINAL)
    private MessageType type;

    protected Message() {
        // no arg constructor is required by JPA
    }

    public Message(User user, String content, MessageType type) {
        this.user = user;
        this.content = content;
        this.type = type;
        this.time = Instant.now();
    }

}
