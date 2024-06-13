package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "persistent_logins")
public class PersistentLogin implements Serializable {

    @Id
    @Column(name = "series", updatable = false, insertable = false, nullable = false)
    private String series;

    @NotNull
    private String token = "";

    @NotNull
    @Column(name = "username", insertable = false, updatable = false)
    private String username = "";

    @NotNull
    private Instant lastUsed;

    public PersistentLogin() {

    }

    public String getSeries() {
        return series;
    }

    public @NotNull String getToken() {
        return token;
    }

    public @NotNull String getUsername() {
        return username;
    }

    public @NotNull Instant getLastUsed() {
        return lastUsed;
    }
}
