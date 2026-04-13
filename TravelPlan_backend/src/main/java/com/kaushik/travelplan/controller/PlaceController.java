package com.kaushik.travelplan.controller;

import com.kaushik.travelplan.entity.Place;
import com.kaushik.travelplan.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/places")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class PlaceController {

    @Autowired
    private PlaceRepository placeRepo;

    @GetMapping("/{city}")
    public List<Place> getPlacesByCity(@PathVariable String city) {
        return placeRepo.findByCityIgnoreCase(city);
    }

    @GetMapping
    public List<Place> getAllPlaces() {
        return placeRepo.findAll();
    }
}
