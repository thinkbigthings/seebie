package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "notification")
public class Notification implements Serializable {

    @Id
    @Column(name="user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Basic
    @NotNull
    private Instant lastSent;


    protected Notification() {

    }

    public Notification(User user) {
        this.user = user;
        lastSent = Instant.now();
    }

    public Notification withLastSent(Instant lastSent) {
        this.lastSent = lastSent;
        return this;
    }

    public Instant getLastSent() {
        return lastSent;
    }

    public User getUser() {
        return user;
    }

}
