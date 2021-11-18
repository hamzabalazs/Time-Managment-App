package com.example.tma;

public enum Priority implements Comparable<Priority>{
    CRITICAL,
    HIGH,
    NORMAL,
    LOW;

    private Priority priority;

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }




}