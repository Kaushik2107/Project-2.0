package com.kaushik.travelplan.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hotels")
public class Hotel {

    @Id
    private String id;

    private String city;
    private String name;
    private int pricePerNight;
    private double rating;
    private String type;        // e.g., "2-star", "3-star", "5-star"
    private double latitude;
    private double longitude;
    private String imageUrl;
    private String amenities;   // comma-separated: "wifi,pool,gym,spa"
    private int maxOccupancy;   // max guests per room (default 2)

    // Getters and Setters
    public String getId() { return id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(int pricePerNight) { this.pricePerNight = pricePerNight; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public int getMaxOccupancy() { return maxOccupancy > 0 ? maxOccupancy : 2; }
    public void setMaxOccupancy(int maxOccupancy) { this.maxOccupancy = maxOccupancy; }
}