package com.kaushik.travelplan.dto;

import java.util.List;

public class TripRequest {

    private String city;
    private int days;
    private int budget;

    // Food customization: "budget", "standard", "premium" (default: standard)
    private String foodType;

    // Cuisine preference for restaurant recommendations
    private String cuisinePreference;   // "Indian", "Chinese", "Italian", etc.
    private String dietPreference;      // "veg", "nonveg", "both"

    // Custom mode: user can send pre-selected hotel/place IDs
    private String selectedHotelId;
    private List<String> selectedPlaceIds;

    // Group trip: number of travelers
    private int travelers;  // default 1 (solo). 2+ = group trip

    // Visitor name (for trip history)
    private String visitorName;

    // Travel date for seasonal pricing (ISO format: "2026-05-15")
    private String travelDate;

    // Multi-city support
    private List<String> cities;        // for multi-city trips
    private List<Integer> daysPerCity;  // days to spend in each city
    private String sourceCity; // origin city (e.g., Ahmedabad)
    private int manualFoodBudget;       // for "manual" foodType

    // New fields
    private String travelStyle;         // e.g., "adventure", "relaxing", "cultural"
    private String pace;                // e.g., "relaxed", "moderate", "fast"
    private List<String> specialInterests; // e.g., "photography", "history", "nightlife"


    // Getters and Setters

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getSourceCity() { return sourceCity; }
    public void setSourceCity(String sourceCity) { this.sourceCity = sourceCity; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public int getBudget() { return budget; }
    public void setBudget(int budget) { this.budget = budget; }

    public String getFoodType() { return foodType; }
    public void setFoodType(String foodType) { this.foodType = foodType; }

    public String getCuisinePreference() { return cuisinePreference; }
    public void setCuisinePreference(String cuisinePreference) { this.cuisinePreference = cuisinePreference; }

    public String getDietPreference() { return dietPreference; }
    public void setDietPreference(String dietPreference) { this.dietPreference = dietPreference; }

    public String getSelectedHotelId() { return selectedHotelId; }
    public void setSelectedHotelId(String selectedHotelId) { this.selectedHotelId = selectedHotelId; }

    public List<String> getSelectedPlaceIds() { return selectedPlaceIds; }
    public void setSelectedPlaceIds(List<String> selectedPlaceIds) { this.selectedPlaceIds = selectedPlaceIds; }

    public int getTravelers() { return travelers > 0 ? travelers : 1; }
    public void setTravelers(int travelers) { this.travelers = travelers; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }

    public String getTravelDate() { return travelDate; }
    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }

    public List<String> getCities() { return cities; }
    public void setCities(List<String> cities) { this.cities = cities; }

    public List<Integer> getDaysPerCity() { return daysPerCity; }
    public void setDaysPerCity(List<Integer> daysPerCity) { this.daysPerCity = daysPerCity; }

    public int getManualFoodBudget() { return manualFoodBudget; }
    public void setManualFoodBudget(int manualFoodBudget) { this.manualFoodBudget = manualFoodBudget; }

    public String getTravelStyle() { return travelStyle; }
    public void setTravelStyle(String travelStyle) { this.travelStyle = travelStyle; }

    public String getPace() { return pace; }
    public void setPace(String pace) { this.pace = pace; }

    public List<String> getSpecialInterests() { return specialInterests; }
    public void setSpecialInterests(List<String> specialInterests) { this.specialInterests = specialInterests; }
}