package com.company.diploma.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.UUID;

@JmixEntity
@Table(name = "ASSIGNMENT", indexes = {
        @Index(name = "IDX_ASSIGNMENT_MENTOR", columnList = "MENTOR_ID"),
        @Index(name = "IDX_ASSIGNMENT_MENTEE", columnList = "MENTEE_ID"),
        @Index(name = "IDX_ASSIGNMENT_REQUEST", columnList = "REQUEST_ID")
})
@Entity
public class Assignment {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "WORKSPACE")
    private String workspace;

    @Column(name = "TOPIC")
    private String topic;

    @JoinColumn(name = "MENTOR_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Participant mentor;

    @JoinColumn(name = "MENTEE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Participant mentee;

    @JoinColumn(name = "REQUEST_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Request request;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}