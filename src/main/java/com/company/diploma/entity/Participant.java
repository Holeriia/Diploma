package com.company.diploma.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.UUID;

@JmixEntity
@Table(name = "PARTICIPANT", indexes = {
        @Index(name = "IDX_PARTICIPANT_USER", columnList = "USER_ID"),
        @Index(name = "IDX_PARTICIPANT_WORKSPACE", columnList = "WORKSPACE_ID")
})
@Entity
public class Participant {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "WORKSPACE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Workspace workspace;

    @Column(name = "MAX_ASSIGNMENTS")
    private Integer maxAssignments;

    @Column(name = "ASSIGNMENTS_NOW")
    private Integer assignmentsNow;

    public Integer getAssignmentsNow() {
        return assignmentsNow;
    }

    public void setAssignmentsNow(Integer assignmentsNow) {
        this.assignmentsNow = assignmentsNow;
    }

    public Integer getMaxAssignments() {
        return maxAssignments;
    }

    public void setMaxAssignments(Integer maxAssignments) {
        this.maxAssignments = maxAssignments;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}