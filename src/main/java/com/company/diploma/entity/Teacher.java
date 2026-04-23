package com.company.diploma.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.UUID;

@JmixEntity
@Table(name = "TEACHER", indexes = {
        @Index(name = "IDX_TEACHER_USER", columnList = "USER_ID"),
        @Index(name = "IDX_TEACHER_DEPARTMENT", columnList = "DEPARTMENT_ID"),
        @Index(name = "IDX_TEACHER_DEGREE", columnList = "DEGREE_ID"),
        @Index(name = "IDX_TEACHER_TITLE", columnList = "TITLE_ID"),
        @Index(name = "IDX_TEACHER_POSITION", columnList = "POSITION_ID")
})
@Entity
public class Teacher {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "DEPARTMENT_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    @JoinColumn(name = "DEGREE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Degree degree;

    @InstanceName
    @JoinColumn(name = "TITLE_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Title title;

    @JoinColumn(name = "POSITION_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Position position;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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

    @InstanceName
    @DependsOnProperties({"user"})
    public String getInstanceName() {
        if (user != null) {
            // Вызываем метод отображения имени из класса User
            return user.getDisplayName();
        }
        return id != null ? id.toString() : "";
    }
}