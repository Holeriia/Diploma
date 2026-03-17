package com.company.diploma.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum RequestStatus implements EnumClass<String> {

    DRAFT("D"),
    IN_REVIEW("IR"),
    CLARIFICATION("C"),
    ACCEPTED("A"),
    FINAL_REJECTED("FR");

    private final String id;

    RequestStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RequestStatus fromId(String id) {
        for (RequestStatus at : RequestStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}