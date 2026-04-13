package com.kaushik.travelplan.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class WeatherSimService {

    private final Random random = new Random();

    public String getSimulatedWeather(String city) {
        String[] conditions = {"Sunny", "Clear Skies", "Slightly Overcast", "Pleasant", "Typical Seasonal Weather"};
        int temp = 22 + random.nextInt(12); // 22-34 degrees

        return conditions[random.nextInt(conditions.length)] + " with an average of " + temp + "°C";
    }
}
