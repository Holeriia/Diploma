package com.company.diploma.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@JmixEntity
@Table(name = "REQUEST_APPROVAL", indexes = {
        @Index(name = "IDX_REQUEST_APPROVAL_REQUEST", columnList = "REQUEST_ID"),
        @Index(name = "IDX_REQUEST_APPROVAL_APPROVER", columnList = "APPROVER_ID")
})
@Entity
public class RequestApproval {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "REQUEST_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Request request;

    @JoinColumn(name = "APPROVER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Participant approver;

    @Column(name = "DECISION")
    private String decision;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ACTION_DATE")
    private Date actionDate;

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public RequestDecision getDecision() {
        return decision == null ? null : RequestDecision.fromId(decision);
    }

    public void setDecision(RequestDecision decision) {
        this.decision = decision == null ? null : decision.getId();
    }

    public Participant getApprover() {
        return approver;
    }

    public void setApprover(Participant approver) {
        this.approver = approver;
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