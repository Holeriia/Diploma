package com.company.diploma.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "TOPIC", indexes = {
        @Index(name = "IDX_TOPIC_AUTHOR", columnList = "AUTHOR_ID")
})
@Entity
public class Topic {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @InstanceName
    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @JoinColumn(name = "AUTHOR_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Participant author;

    @JoinTable(name = "TOPIC_INTEREST_LINK",
            joinColumns = @JoinColumn(name = "TOPIC_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "INTEREST_ID", referencedColumnName = "ID"))
    @ManyToMany
    private List<Interest> interests;

    @Column(name = "STATUS")
    private String status;

    public Participant getAuthor() {
        return author;
    }

    public void setAuthor(Participant author) {
        this.author = author;
    }

    public TopicStatus getStatus() {
        return status == null ? null : TopicStatus.fromId(status);
    }

    public void setStatus(TopicStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
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