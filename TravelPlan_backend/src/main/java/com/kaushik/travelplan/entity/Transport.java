package com.kaushik.travelplan.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "transports")
public class Transport {

    @Id
    private String id;

    private String city;
    private String mode;        // "auto", "cab", "bus", "metro"
    private int pricePerKm;
    private int capacity;       // max passengers: auto=3, cab=4, bus=50, metro=unlimited
    private boolean available;
    private int baseFare;       // minimum fare

    // Getters and Setters
    public String getId() { return id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public int getPricePerKm() { return pricePerKm; }
    public void setPricePerKm(int pricePerKm) { this.pricePerKm = pricePerKm; }

    public int getCapacity() { return capacity > 0 ? capacity : 4; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getBaseFare() { return baseFare; }
    public void setBaseFare(int baseFare) { this.baseFare = baseFare; }
}
