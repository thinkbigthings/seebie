package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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


    public Notification() {

    }

    public Instant getLastSent() {
        return lastSent;
    }

    public void setLastSent(Instant lastSent) {
        this.lastSent = lastSent;
    }
}
