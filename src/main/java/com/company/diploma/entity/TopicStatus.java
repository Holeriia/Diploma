package com.company.diploma.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum TopicStatus implements EnumClass<String> {

    FREE("F"),
    BUSY("B");

    private final String id;

    TopicStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TopicStatus fromId(String id) {
        for (TopicStatus at : TopicStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}