package com.company.diploma.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum AssignmentStatus implements EnumClass<String> {

    NOT_APPROVED("NA"),
    FOR_APPROVAL("FA"),
    APPROVED("A");

    private final String id;

    AssignmentStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static AssignmentStatus fromId(String id) {
        for (AssignmentStatus at : AssignmentStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}