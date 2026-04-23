package com.company.diploma.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.NumberFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@JmixEntity
@Table(name = "STUDENT", indexes = {
        @Index(name = "IDX_STUDENT_USER", columnList = "USER_ID"),
        @Index(name = "IDX_STUDENT_GROUP", columnList = "GROUP_ID"),
        @Index(name = "IDX_STUDENT_DEPARTMENT", columnList = "DEPARTMENT_ID")
})
@Entity
public class Student {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotNull
    @JoinColumn(name = "USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "GROUP_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    @NumberFormat(pattern = "0.00")
    @Column(name = "AVERAGE_SCORE")
    private Double averageScore;

    @JoinColumn(name = "DEPARTMENT_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    @Column(name = "BASIS_OF_LEARNING")
    private String basisOfLearning;

    @Column(name = "SNILS")
    private String snils;

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public BasisOfLearning getBasisOfLearning() {
        return basisOfLearning == null ? null : BasisOfLearning.fromId(basisOfLearning);
    }

    public void setBasisOfLearning(BasisOfLearning basisOfLearning) {
        this.basisOfLearning = basisOfLearning == null ? null : basisOfLearning.getId();
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
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