package com.kaushik.travelplan.repository;

import com.kaushik.travelplan.entity.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    List<Restaurant> findByCityIgnoreCase(String city);
    List<Restaurant> findByCityIgnoreCaseAndCategory(String city, String category);
    List<Restaurant> findByCityIgnoreCaseAndMealType(String city, String mealType);
}
