package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @see com.seebie.server.entity.SleepSession for more details on the primary key and sequence generator.
 */
@Entity
@Table(name = "challenge")
public class Challenge implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "challenge_generator")
    @SequenceGenerator(name="challenge_generator", sequenceName = "challenge_sequence", allocationSize = 50)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private String name = "";

    @NotNull
    private String description = "";

    @Basic
    @NotNull
    private LocalDate start = LocalDate.now();

    @Basic
    @NotNull
    private LocalDate finish = LocalDate.now();


    public Challenge() {
        // no arg constructor is required by JPA
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String notes) {
        this.name = notes;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getFinish() {
        return finish;
    }

    public void setFinish(LocalDate finish) {
        this.finish = finish;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
