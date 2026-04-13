package com.kaushik.travelplan.controller;

import com.kaushik.travelplan.entity.Restaurant;
import com.kaushik.travelplan.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/{city}")
    public List<Restaurant> getRestaurantsByCity(@PathVariable String city) {
        return restaurantService.getAllByCity(city);
    }
}
