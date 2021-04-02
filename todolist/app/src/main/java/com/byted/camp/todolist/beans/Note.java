package com.byted.camp.todolist.beans;

import java.io.Serializable;

public class Note implements Serializable {

    public final long id;
    private State state;
    private String content;
    private Priority priority;
    private int year;
    private int month;
    private int day;

    public Note(long id) {
        this.id = id;
    }

    public int getYear(){return year;}

    public void setYear(int year){this.year = year;}

    public int getMonth(){return month;}

    public void setMonth(int month){this.month = month;}

    public int getDay(){return day;}

    public void setDay(int day){this.day = day;}

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
