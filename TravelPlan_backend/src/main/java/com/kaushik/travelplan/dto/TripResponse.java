package com.kaushik.travelplan.dto;

import com.kaushik.travelplan.entity.Hotel;
import com.kaushik.travelplan.entity.Place;
import com.kaushik.travelplan.entity.Restaurant;

import java.util.List;

public class TripResponse {

    private int totalCost;
    private int perPersonCost;
    private int travelers;
    private Hotel hotel;
    private List<Place> places;

    // Budget breakdown
    private BudgetBreakdown budgetBreakdown;

    // Legacy fields for backward compatibility
    private int hotelCost;
    private int foodCost;
    private int travelCost;
    private int placesCost;

    // Restaurant recommendations
    private List<Restaurant> restaurants;
    private int restaurantCost;

    // Transport details
    private String transportMode;
    private int transportCost;
    private double totalDistanceKm;

    // Day-wise itinerary (structured)
    private List<DayPlan> dayWisePlanDetailed;

    // Legacy day-wise plan (string-based)
    private List<String> dayWisePlan;

    // Suggestion engine output
    private String suggestionMessage;

    // Trip quality score
    private TripScore tripScore;

    // Seasonal info
    private String seasonInfo;
    private double seasonMultiplier;

    // Group trip breakdown
    private GroupCostBreakdown groupCostBreakdown;

    // Message field for safe handling
    private String message;

    // Advanced analysis
    private String tripVibe;
    private String weatherForecast;
    private String arrivalDetails; // info about transport from source city
    private String cityImageUrl;


    public int getTotalCost() { return totalCost; }
    public void setTotalCost(int totalCost) { this.totalCost = totalCost; }

    public String getArrivalDetails() { return arrivalDetails; }
    public void setArrivalDetails(String arrivalDetails) { this.arrivalDetails = arrivalDetails; }

    public int getPerPersonCost() { return perPersonCost; }
    public void setPerPersonCost(int perPersonCost) { this.perPersonCost = perPersonCost; }

    public int getTravelers() { return travelers; }
    public void setTravelers(int travelers) { this.travelers = travelers; }

    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }

    public List<Place> getPlaces() { return places; }
    public void setPlaces(List<Place> places) { this.places = places; }

    public BudgetBreakdown getBudgetBreakdown() { return budgetBreakdown; }
    public void setBudgetBreakdown(BudgetBreakdown budgetBreakdown) { this.budgetBreakdown = budgetBreakdown; }

    public int getHotelCost() { return hotelCost; }
    public void setHotelCost(int hotelCost) { this.hotelCost = hotelCost; }

    public int getFoodCost() { return foodCost; }
    public void setFoodCost(int foodCost) { this.foodCost = foodCost; }

    public int getTravelCost() { return travelCost; }
    public void setTravelCost(int travelCost) { this.travelCost = travelCost; }

    public int getPlacesCost() { return placesCost; }
    public void setPlacesCost(int placesCost) { this.placesCost = placesCost; }

    public List<Restaurant> getRestaurants() { return restaurants; }
    public void setRestaurants(List<Restaurant> restaurants) { this.restaurants = restaurants; }

    public int getRestaurantCost() { return restaurantCost; }
    public void setRestaurantCost(int restaurantCost) { this.restaurantCost = restaurantCost; }

    public String getTransportMode() { return transportMode; }
    public void setTransportMode(String transportMode) { this.transportMode = transportMode; }

    public int getTransportCost() { return transportCost; }
    public void setTransportCost(int transportCost) { this.transportCost = transportCost; }

    public double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }

    public List<DayPlan> getDayWisePlanDetailed() { return dayWisePlanDetailed; }
    public void setDayWisePlanDetailed(List<DayPlan> dayWisePlanDetailed) { this.dayWisePlanDetailed = dayWisePlanDetailed; }

    public List<String> getDayWisePlan() { return dayWisePlan; }
    public void setDayWisePlan(List<String> dayWisePlan) { this.dayWisePlan = dayWisePlan; }

    public String getSuggestionMessage() { return suggestionMessage; }
    public void setSuggestionMessage(String suggestionMessage) { this.suggestionMessage = suggestionMessage; }

    public TripScore getTripScore() { return tripScore; }
    public void setTripScore(TripScore tripScore) { this.tripScore = tripScore; }

    public String getSeasonInfo() { return seasonInfo; }
    public void setSeasonInfo(String seasonInfo) { this.seasonInfo = seasonInfo; }

    public double getSeasonMultiplier() { return seasonMultiplier; }
    public void setSeasonMultiplier(double seasonMultiplier) { this.seasonMultiplier = seasonMultiplier; }

    public GroupCostBreakdown getGroupCostBreakdown() { return groupCostBreakdown; }
    public void setGroupCostBreakdown(GroupCostBreakdown groupCostBreakdown) { this.groupCostBreakdown = groupCostBreakdown; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTripVibe() { return tripVibe; }
    public void setTripVibe(String tripVibe) { this.tripVibe = tripVibe; }

    public String getWeatherForecast() { return weatherForecast; }
    public void setWeatherForecast(String weatherForecast) { this.weatherForecast = weatherForecast; }
    public String getCityImageUrl() { return cityImageUrl; }
    public void setCityImageUrl(String cityImageUrl) { this.cityImageUrl = cityImageUrl; }
}