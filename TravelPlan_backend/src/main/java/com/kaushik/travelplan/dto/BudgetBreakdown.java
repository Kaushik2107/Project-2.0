package com.kaushik.travelplan.dto;

public class BudgetBreakdown {
    private int hotelBudget;
    private int foodBudget;
    private int travelBudget;
    private int placesBudget;
    private int hotelActual;
    private int foodActual;
    private int travelActual;
    private int placesActual;
    private int savings;

    public int getHotelBudget() { return hotelBudget; }
    public void setHotelBudget(int hotelBudget) { this.hotelBudget = hotelBudget; }

    public int getFoodBudget() { return foodBudget; }
    public void setFoodBudget(int foodBudget) { this.foodBudget = foodBudget; }

    public int getTravelBudget() { return travelBudget; }
    public void setTravelBudget(int travelBudget) { this.travelBudget = travelBudget; }

    public int getPlacesBudget() { return placesBudget; }
    public void setPlacesBudget(int placesBudget) { this.placesBudget = placesBudget; }

    public int getHotelActual() { return hotelActual; }
    public void setHotelActual(int hotelActual) { this.hotelActual = hotelActual; }

    public int getFoodActual() { return foodActual; }
    public void setFoodActual(int foodActual) { this.foodActual = foodActual; }

    public int getTravelActual() { return travelActual; }
    public void setTravelActual(int travelActual) { this.travelActual = travelActual; }

    public int getPlacesActual() { return placesActual; }
    public void setPlacesActual(int placesActual) { this.placesActual = placesActual; }

    public int getSavings() { return savings; }
    public void setSavings(int savings) { this.savings = savings; }
}
