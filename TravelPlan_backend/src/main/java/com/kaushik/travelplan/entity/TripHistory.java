package com.kaushik.travelplan.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "trip_history")
public class TripHistory {

    @Id
    private String id;

    private String visitorName;
    private String city;
    private int budget;
    private int days;
    private int travelers;
    private String foodType;
    private int totalCost;
    private int perPersonCost;
    private String hotelName;
    private String planSummary;     // JSON string of the full response
    private LocalDateTime createdAt;

    // Getters and Setters
    public String getId() { return id; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public int getBudget() { return budget; }
    public void setBudget(int budget) { this.budget = budget; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public int getTravelers() { return travelers; }
    public void setTravelers(int travelers) { this.travelers = travelers; }

    public String getFoodType() { return foodType; }
    public void setFoodType(String foodType) { this.foodType = foodType; }

    public int getTotalCost() { return totalCost; }
    public void setTotalCost(int totalCost) { this.totalCost = totalCost; }

    public int getPerPersonCost() { return perPersonCost; }
    public void setPerPersonCost(int perPersonCost) { this.perPersonCost = perPersonCost; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public String getPlanSummary() { return planSummary; }
    public void setPlanSummary(String planSummary) { this.planSummary = planSummary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
