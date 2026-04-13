package com.kaushik.travelplan.dto;

import java.util.Map;

/**
 * Represents a single step in a travel timeline.
 */
public class TimelineStep {
    private String startTime;
    private String endTime;
    private String activityType; // "Breakfast", "Travel", "Visit", "Lunch", "Dinner", "Stay"
    private String title;
    private Map<String, Object> details;

    public TimelineStep() {}

    public TimelineStep(String startTime, String endTime, String activityType, String title, Map<String, Object> details) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.activityType = activityType;
        this.title = title;
        this.details = details;
    }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
