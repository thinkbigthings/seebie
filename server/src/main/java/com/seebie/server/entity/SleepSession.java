package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sleep_session", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "date_awakened"})})
public class SleepSession implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @NotNull
    private String notes = "";

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "sleep_session_tag",
            joinColumns = @JoinColumn(name = "sleep_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @NotNull
    @Column(name="date_awakened")
    private LocalDate dateAwakened;

    @NotNull
    private int minutes = 0;

    @Basic
    private boolean outOfBed = false;

    @Basic
    private boolean ignored = false;

    protected SleepSession() {
        // no arg constructor is required by JPA
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public LocalDate getDateAwakened() {
        return dateAwakened;
    }

    public void setDateAwakened(LocalDate dateAwakened) {
        this.dateAwakened = dateAwakened;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public boolean isOutOfBed() {
        return outOfBed;
    }

    public void setOutOfBed(boolean outOfBed) {
        this.outOfBed = outOfBed;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }
}
