package com.byted.camp.todolist.beans;

import android.graphics.Color;

public enum Priority {
    High(2, Color.CYAN),
    Medium(1, Color.WHITE),
    Low(0, Color.GREEN);

    public int intValue;
    public int color;

    Priority(int intValue, int color) {
        this.intValue = intValue;
        this.color = color;
    }

    public static Priority from(int intValue) {
        for (Priority priority : Priority.values()) {
            if (priority.intValue == intValue) {
                return priority;
            }
        }
        return Priority.Low; // default
    }
}
