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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

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
