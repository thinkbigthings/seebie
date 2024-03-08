package com.seebie.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "SPRING_SESSION", uniqueConstraints = {@UniqueConstraint(columnNames = {"PRINCIPAL_NAME"})})
public class Session implements Serializable {

    @Id
    @Column(name = "PRIMARY_ID", updatable = false, insertable = false, nullable = false)
    private String primaryId;

    @NotNull
    private String sessionId = "";

    @NotNull
    @Column(name = "principal_name", insertable = false, updatable = false)
    private String principalName = "";

    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "principal_name", referencedColumnName = "username")
    private User user;

    protected Session() {

    }

}
