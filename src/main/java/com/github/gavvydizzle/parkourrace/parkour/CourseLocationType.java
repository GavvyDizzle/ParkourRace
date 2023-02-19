package com.github.gavvydizzle.parkourrace.parkour;

import javax.annotation.Nullable;

public enum CourseLocationType {
    FINISH,
    START,
    TELEPORT;

    @Nullable
    public static CourseLocationType get(String str) {
        for (CourseLocationType courseLocationType : CourseLocationType.values()) {
            if (courseLocationType.toString().equalsIgnoreCase(str)) return courseLocationType;
        }
        return null;
    }
}
