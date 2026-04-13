package com.kaushik.travelplan.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "restaurants")
public class Restaurant {

    @Id
    private String id;

    private String city;
    private String name;
    private String cuisine;         // e.g., "Indian", "Chinese", "Italian", "Street Food"
    private int pricePerMeal;       // average cost per person per meal
    private double rating;
    private String category;        // "veg", "nonveg", "both"
    private String mealType;        // "breakfast", "lunch", "dinner", "all"
    private double latitude;
    private double longitude;
    private String imageUrl;

    // Getters and Setters
    public String getId() { return id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    public int getPricePerMeal() { return pricePerMeal; }
    public void setPricePerMeal(int pricePerMeal) { this.pricePerMeal = pricePerMeal; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
