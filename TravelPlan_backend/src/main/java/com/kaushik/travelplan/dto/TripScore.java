package com.kaushik.travelplan.dto;

public class TripScore {
    private int overallScore;       // 0-100
    private int budgetUtilization;  // how well budget was used (0-100)
    private int diversityScore;     // variety of place categories (0-100)
    private int hotelQuality;       // hotel rating vs budget (0-100)
    private int foodQuality;        // restaurant quality (0-100)
    private int itineraryBalance;   // places per day balance (0-100)
    private String verdict;         // "Excellent", "Good", "Average", "Poor"

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

    public int getBudgetUtilization() { return budgetUtilization; }
    public void setBudgetUtilization(int budgetUtilization) { this.budgetUtilization = budgetUtilization; }

    public int getDiversityScore() { return diversityScore; }
    public void setDiversityScore(int diversityScore) { this.diversityScore = diversityScore; }

    public int getHotelQuality() { return hotelQuality; }
    public void setHotelQuality(int hotelQuality) { this.hotelQuality = hotelQuality; }

    public int getFoodQuality() { return foodQuality; }
    public void setFoodQuality(int foodQuality) { this.foodQuality = foodQuality; }

    public int getItineraryBalance() { return itineraryBalance; }
    public void setItineraryBalance(int itineraryBalance) { this.itineraryBalance = itineraryBalance; }

    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }
}
