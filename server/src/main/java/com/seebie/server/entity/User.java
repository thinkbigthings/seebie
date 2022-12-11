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

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "user")
    private Set<Session> sessions = new HashSet<>();

    protected User() {
        // no arg constructor is required by JPA
    }

    public User(String name, String display) {
        username = name;
        displayName = display;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public Instant getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Instant registration) {
        this.registrationTime = registration;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public Set<Session> getSessions() {
        return sessions;
    }

}
