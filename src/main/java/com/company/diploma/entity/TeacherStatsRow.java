package com.company.diploma.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeacherStatsRow {
    private UUID id;
    private String teacherName;
    private Map<String, Integer> groupCounts = new HashMap<>();

    public TeacherStatsRow() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public void setGroupCount(String groupName, Integer count) {
        groupCounts.put(groupName, count);
    }

    public Integer getGroupCount(String groupName) {
        return groupCounts.getOrDefault(groupName, 0);
    }

    public Integer getTotal() {
        return groupCounts.values().stream().mapToInt(Integer::intValue).sum();
    }
}