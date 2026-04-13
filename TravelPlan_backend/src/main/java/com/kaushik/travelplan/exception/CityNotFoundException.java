package com.kaushik.travelplan.exception;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String city) {
        super("No data found for city: " + city + ". Please check the city name or add data.");
    }
}
