package com.company.diploma.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.UUID;

@JmixEntity
@Table(name = "ASSIGNMENT", indexes = {
        @Index(name = "IDX_ASSIGNMENT_MENTOR", columnList = "MENTOR_ID"),
        @Index(name = "IDX_ASSIGNMENT_MENTEE", columnList = "MENTEE_ID"),
        @Index(name = "IDX_ASSIGNMENT_REQUEST", columnList = "REQUEST_ID"),
        @Index(name = "IDX_ASSIGNMENT_TOPIC", columnList = "TOPIC_ID"),
        @Index(name = "IDX_ASSIGNMENT_", columnList = "")
})
@Entity
public class Assignment {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "WORKSPACE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Workspace workspace;

    @JoinColumn(name = "TOPIC_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Topic topic;

    @JoinColumn(name = "MENTOR_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Participant mentor;

    @JoinColumn(name = "MENTEE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Participant mentee;

    @JoinColumn(name = "REQUEST_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Request request;

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Participant getMentee() {
        return mentee;
    }

    public void setMentee(Participant mentee) {
        this.mentee = mentee;
    }

    public Participant getMentor() {
        return mentor;
    }

    public void setMentor(Participant mentor) {
        this.mentor = mentor;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}