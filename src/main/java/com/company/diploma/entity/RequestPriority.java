package com.company.diploma.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.UUID;

@JmixEntity
@Table(name = "REQUEST_PRIORITY", indexes = {
        @Index(name = "IDX_REQUEST_PRIORITY_REQUEST", columnList = "REQUEST_ID"),
        @Index(name = "IDX_REQUEST_PRIORITY_PARTICIPANT", columnList = "PARTICIPANT_ID")
})
@Entity
public class RequestPriority {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "REQUEST_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Request request;

    @Column(name = "PRIORITY_NUMBER")
    private Integer priorityNumber;

    @JoinColumn(name = "PARTICIPANT_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Participant participant;

    public Integer getPriorityNumber() {
        return priorityNumber;
    }

    public void setPriorityNumber(Integer priorityNumber) {
        this.priorityNumber = priorityNumber;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}