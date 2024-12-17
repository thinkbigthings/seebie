package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "sleep_session")
public class SleepSession implements Serializable {

    /**
     * We should prefer SEQUENCE over IDENTITY. Hibernate silently disables batch inserts for IDENTITY generators.
     *
     * The SequenceGenerator allocationSize MUST match the increment of the sequence
     * A good explanation of how it works is here https://vladmihalcea.com/jpa-entity-identifier-sequence/
     * but in a nutshell JPA will get one sequence value and allocate more values itself internally
     * and when it needs more values will call the database sequence for the next value.
     *
     * Also note the batch size property spring.jpa.properties.hibernate.jdbc.batch_size=50
     *
     * Note that there's another optimization for using hilo,
     * see https://vladmihalcea.com/the-hilo-algorithm/
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sleep_session_generator")
    @SequenceGenerator(name="sleep_session_generator", sequenceName = "sleep_session_sequence", allocationSize = 50)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private String notes = "";

    @Basic
    private int minutesAwake = 0;

    @Basic
    @NotNull
    private LocalDateTime startTime = LocalDateTime.now();

    @Basic
    @NotNull
    @Column(name="stop_time")
    private LocalDateTime stopTime = LocalDateTime.now();

    /**
     * We want to keep the duration in the database, so we can do things like query against it,
     * and have a constraint to ensure it is always correct.
     * And only calculated in one place so there is only one place to update the calculation.
     */
    @Column
    private int minutesAsleep;

    @NotNull
    private String zoneId;

    public SleepSession() {
        // no arg constructor is required by JPA
    }

    public Long getId() {
        return id;
    }

    public String getZoneId() {
        return zoneId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getStopTime() {
        return stopTime;
    }

    public int getMinutesAsleep() {
        return minutesAsleep;
    }

    public void setSleepData(int minutesAwake, String notes, LocalDateTime start, LocalDateTime stop, int minutesAsleep, String zoneId) {
        this.minutesAwake = minutesAwake;
        this.notes = notes;
        this.startTime = start;
        this.stopTime = stop;
        this.zoneId = zoneId;
        this.minutesAsleep = minutesAsleep;
    }

    public String getNotes() {
        return notes;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getMinutesAwake() {
        return minutesAwake;
    }

}
