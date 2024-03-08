package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @Column(unique=true)
    @NotNull
    @Size(min = 3, message = "must be at least three characters")
    private String username = "";

    @NotNull
    private String password = "";

    @NotNull
    @Column(unique=true)
    @Size(min = 3, message = "must be at least three characters")
    private String email = "";

    @NotNull
    @Size(min = 3, message = "must be at least three characters")
    @Column(name="display_name")
    private String displayName = "";

    @Basic
    private boolean enabled = false;

    @Basic
    @NotNull
    private Instant registrationTime;

    @ElementCollection(fetch=FetchType.LAZY, targetClass=Role.class)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_id")
    @Enumerated(EnumType.ORDINAL)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "user")
    private Set<Session> sessions = new HashSet<>();

    // If we start using more user settings, this could be moved to a settings entity.
    @Basic
    private boolean notificationsEnabled = false;

    protected User() {
        // no arg constructor is required by JPA
    }

    public User(String name, String displayName, String email, String encryptedPassword) {
        this.username = name;
        this.displayName = displayName;
        this.email = email;
        this.password = encryptedPassword;

        this.enabled = true;
        this.registrationTime = Instant.now();
        this.roles.add(Role.USER);
    }

    public void setUserData(String email, String displayName, boolean notificationsEnabled) {
        this.email = email;
        this.displayName = displayName;
        this.notificationsEnabled = notificationsEnabled;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Instant getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Instant registration) {
        this.registrationTime = registration;
    }

    public Set<Session> getSessions() {
        return sessions;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

}
