package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sleep_session")
public class SleepSession implements Serializable {

    /**
     * We should prefer SEQUENCE over IDENTITY. Hibernate silently disables batch inserts for IDENTITY generators.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sleep_session_generator")
    @SequenceGenerator(name="sleep_session_generator", sequenceName = "sleep_session_sequence", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
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

    @Basic
    private int minutesAwake = 0;

    @Basic
    @NotNull
    private ZonedDateTime startTime = ZonedDateTime.now();

    @Basic
    @NotNull
    @Column(name="stop_time")
    private ZonedDateTime stopTime = ZonedDateTime.now();

    /**
     * We want to keep the duration in the database, so we can do things like query against it,
     * and have a constraint to ensure it is always correct.
     * And only calculated in one place so there is only one place to update the calculation.
     */
    @Column
    private int minutesAsleep;

    public SleepSession() {
        // no arg constructor is required by JPA
    }

    public Long getId() {
        return id;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getStopTime() {
        return stopTime;
    }

    public int getMinutesAsleep() {
        return minutesAsleep;
    }

    public void setSleepData(int minutesAwake, String notes, Set<Tag> newTags, ZonedDateTime start, ZonedDateTime stop) {

        this.minutesAwake = minutesAwake;
        this.notes = notes;
        this.startTime = start.truncatedTo(ChronoUnit.MINUTES);
        this.stopTime = stop.truncatedTo(ChronoUnit.MINUTES);

        // this is calculated here and not in the database
        // (despite the calculation being done in the database anyway to check the constraint)
        // because after saving, database computed values are not available until after the transaction closes
        // and the returned entity after save won't have the updated value
        this.minutesAsleep = (int)Duration.between(startTime, stopTime).abs().toMinutes() - minutesAwake;

        this.tags.clear();
        this.tags.addAll(newTags);
    }

    public String getNotes() {
        return notes;
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

    public int getMinutesAwake() {
        return minutesAwake;
    }

}
