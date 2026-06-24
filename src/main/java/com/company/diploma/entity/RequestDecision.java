package com.company.diploma.entity;

import io.jmix.core.metamodel.datatype.EnumClass;
import org.springframework.lang.Nullable;


public enum RequestDecision implements EnumClass<String> {

    APPROVE("A"),
    REJECT("R"),
    COMMENT("C");

    private final String id;

    RequestDecision(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RequestDecision fromId(String id) {
        for (RequestDecision at : RequestDecision.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}