package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private String notes = "";

    @ManyToMany
    @JoinTable(
            name = "sleep_session_tag",
            joinColumns = @JoinColumn(name = "sleep_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @NotNull
    @Column(name="date_awakened")
    private LocalDate dateAwakened;

    @Basic
    private int minutes = 0;

    @Basic
    private int outOfBed = 0;

    @Basic
    @NotNull
    private ZonedDateTime startTime = ZonedDateTime.now();

    @Basic
    @NotNull
    private ZonedDateTime stopTime = ZonedDateTime.now();

    // this is computed inside the database, so is readable but not writable
    @Column(insertable = false, updatable = false)
    private int durationMinutes;

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getStopTime() {
        return stopTime;
    }

    public void setStopTime(ZonedDateTime stopTime) {
        this.stopTime = stopTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public SleepSession() {
        // no arg constructor is required by JPA
    }

    public void setSleepData(LocalDate dateAwakened, int minutes, int outOfBed, String notes, Set<Tag> newTags,
                             ZonedDateTime start, ZonedDateTime stop) {

        setDateAwakened(dateAwakened);
        setMinutes(minutes);
        setOutOfBed(outOfBed);
        setNotes(notes);
        setStartTime(start);
        setStopTime(stop);

        tags.clear();
        tags.addAll(newTags);
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

    public int getOutOfBed() {
        return outOfBed;
    }

    public void setOutOfBed(int outOfBed) {
        this.outOfBed = outOfBed;
    }

}
