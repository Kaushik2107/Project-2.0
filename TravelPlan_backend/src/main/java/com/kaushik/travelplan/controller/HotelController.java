package com.kaushik.travelplan.controller;

import com.kaushik.travelplan.entity.Hotel;
import com.kaushik.travelplan.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class HotelController {

    @Autowired
    private HotelRepository hotelRepo;

    @GetMapping("/{city}")
    public List<Hotel> getHotelsByCity(@PathVariable String city) {
        return hotelRepo.findByCityIgnoreCase(city);
    }

    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelRepo.findAll();
    }
}
