package com.seebie.server.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "app_user", uniqueConstraints = {@UniqueConstraint(columnNames = {"public_id"})})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @Column(unique = true, name = "public_id", updatable = false, insertable = true, nullable = false)
    private UUID publicId;

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
    @NotNull
    private Instant registrationTime;

    @ElementCollection(fetch=FetchType.LAZY, targetClass=Role.class)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_id")
    @Enumerated(EnumType.ORDINAL)
    private Set<Role> roles = new HashSet<>();

    // If we start using more user settings, this could be moved to a settings entity.
    @Basic
    private boolean notificationsEnabled = false;

    /**
     * Since this field isn't a primary key, we can't use Hibernate's generator annotations.
     * Once Postgres supports UUID v7 natively (in PG 18)
     * we can just make that the default value on the column and remove this.
     * Alternatively if we migrate the publicId to be the PK, we can use the generator annotations.
     */
    @PrePersist
    public void ensurePublicIdUuidV7() {
        if (publicId == null) {
            publicId = UuidCreator.getTimeOrderedEpoch();
        }
    }

    protected User() {
        // no arg constructor is required by JPA
    }

    public User(String displayName, String email, String encryptedPassword) {
        this.displayName = displayName;
        this.email = email;
        this.password = encryptedPassword;
        this.registrationTime = Instant.now();
        this.roles.add(Role.USER);
    }

    public User withUserData(String displayName, boolean notificationsEnabled) {
        this.displayName = displayName;
        this.notificationsEnabled = notificationsEnabled;
        return this;
    }

    public String getPublicId() {
        return publicId.toString();
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

    public Set<Role> getRoles() {
        return roles;
    }

    public Instant getRegistrationTime() {
        return registrationTime;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

}
