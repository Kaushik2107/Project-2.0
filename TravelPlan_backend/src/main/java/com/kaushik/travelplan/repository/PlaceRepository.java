package com.kaushik.travelplan.repository;

import com.kaushik.travelplan.entity.Place;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PlaceRepository extends MongoRepository<Place, String> {
    List<Place> findByCityIgnoreCase(String city);
}