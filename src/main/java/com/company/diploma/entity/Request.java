package com.company.diploma.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.Composition;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "REQUEST", indexes = {
        @Index(name = "IDX_REQUEST_INITIATOR", columnList = "INITIATOR_ID"),
        @Index(name = "IDX_REQUEST_WORKSPACE", columnList = "WORKSPACE_ID")
})
@Entity
public class Request {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @InstanceName
    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    @Lob
    private String description;

    @JoinColumn(name = "WORKSPACE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Workspace workspace;

    @JoinColumn(name = "INITIATOR_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Participant initiator;

    @Column(name = "STATUS")
    private String status;

    @Composition
    @OneToMany(mappedBy = "request")
    private List<RequestPriority> priorities;

    @Composition
    @OneToMany(mappedBy = "request")
    private List<RequestComment> comments;

    public List<RequestComment> getComments() {
        return comments;
    }

    public void setComments(List<RequestComment> comments) {
        this.comments = comments;
    }

    public List<RequestPriority> getPriorities() {
        return priorities;
    }

    public void setPriorities(List<RequestPriority> priorities) {
        this.priorities = priorities;
    }

    public RequestStatus getStatus() {
        return status == null ? null : RequestStatus.fromId(status);
    }

    public void setStatus(RequestStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public Participant getInitiator() {
        return initiator;
    }

    public void setInitiator(Participant initiator) {
        this.initiator = initiator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}