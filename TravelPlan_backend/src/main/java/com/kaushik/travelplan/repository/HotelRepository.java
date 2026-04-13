package com.kaushik.travelplan.repository;

import com.kaushik.travelplan.entity.Hotel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface HotelRepository extends MongoRepository<Hotel, String> {
        List<Hotel> findByCityIgnoreCase(String city);
}