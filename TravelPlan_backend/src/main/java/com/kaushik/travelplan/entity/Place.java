package com.kaushik.travelplan.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "places")
public class Place {

    @Id
    private String id;

    private String city;
    private String name;
    private int entryFee;
    private double rating;
    private String category;    // e.g., "beach", "fort", "market", "temple", "museum"
    private double latitude;
    private double longitude;
    private String imageUrl;
    private String bestTime;    // e.g., "morning", "evening", "anytime"
    private int durationMinutes; // recommended time to spend (in minutes)
    private boolean indoor;     // true = indoor (weather-proof), false = outdoor

    // Getters and Setters
    public String getId() { return id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getEntryFee() { return entryFee; }
    public void setEntryFee(int entryFee) { this.entryFee = entryFee; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getBestTime() { return bestTime; }
    public void setBestTime(String bestTime) { this.bestTime = bestTime; }

    public int getDurationMinutes() { return durationMinutes > 0 ? durationMinutes : 60; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public boolean isIndoor() { return indoor; }
    public void setIndoor(boolean indoor) { this.indoor = indoor; }
}