package com.kaushik.travelplan.dto;

import java.util.List;

public class ComparisonRequest {
    private String city;
    private int days;
    private List<Integer> budgets;   // e.g., [10000, 15000, 20000]
    private String foodType;
    private int travelers;
    private String travelDate;

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public List<Integer> getBudgets() { return budgets; }
    public void setBudgets(List<Integer> budgets) { this.budgets = budgets; }

    public String getFoodType() { return foodType; }
    public void setFoodType(String foodType) { this.foodType = foodType; }

    public int getTravelers() { return travelers; }
    public void setTravelers(int travelers) { this.travelers = travelers; }

    public String getTravelDate() { return travelDate; }
    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }
}
