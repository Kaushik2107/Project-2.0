package com.kaushik.travelplan.dto;

import com.kaushik.travelplan.entity.Place;
import com.kaushik.travelplan.entity.Restaurant;

import java.util.List;

public class DayPlan {
    private int day;
    private String title;       // "Day 1 — Arrival & Exploration"
    private String hotelAction; // "Check-in", "Stay", "Check-out"
    private List<Place> places;
    private Restaurant breakfast;
    private Restaurant lunch;
    private Restaurant dinner;
    private String transportMode;
    private double distanceKm;
    private int dayCost;
    private String notes;       // "Rainy day — indoor activities suggested"
    private List<TimelineStep> timeline;

    public List<TimelineStep> getTimeline() { return timeline; }
    public void setTimeline(List<TimelineStep> timeline) { this.timeline = timeline; }

    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getHotelAction() { return hotelAction; }
    public void setHotelAction(String hotelAction) { this.hotelAction = hotelAction; }

    public List<Place> getPlaces() { return places; }
    public void setPlaces(List<Place> places) { this.places = places; }

    public Restaurant getBreakfast() { return breakfast; }
    public void setBreakfast(Restaurant breakfast) { this.breakfast = breakfast; }

    public Restaurant getLunch() { return lunch; }
    public void setLunch(Restaurant lunch) { this.lunch = lunch; }

    public Restaurant getDinner() { return dinner; }
    public void setDinner(Restaurant dinner) { this.dinner = dinner; }

    public String getTransportMode() { return transportMode; }
    public void setTransportMode(String transportMode) { this.transportMode = transportMode; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public int getDayCost() { return dayCost; }
    public void setDayCost(int dayCost) { this.dayCost = dayCost; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
