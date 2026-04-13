package com.kaushik.travelplan.repository;

import com.kaushik.travelplan.entity.Transport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransportRepository extends MongoRepository<Transport, String> {
    List<Transport> findByCityIgnoreCase(String city);
    List<Transport> findByCityIgnoreCaseAndAvailable(String city, boolean available);
}
