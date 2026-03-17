package com.company.diploma.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum BasisOfLearning implements EnumClass<String> {

    BUDGET("B"),
    PAID("P");

    private final String id;

    BasisOfLearning(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static BasisOfLearning fromId(String id) {
        for (BasisOfLearning at : BasisOfLearning.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}