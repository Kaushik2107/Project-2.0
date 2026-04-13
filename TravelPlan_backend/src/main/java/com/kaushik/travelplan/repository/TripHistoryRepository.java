package com.kaushik.travelplan.repository;

import com.kaushik.travelplan.entity.TripHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TripHistoryRepository extends MongoRepository<TripHistory, String> {
    List<TripHistory> findByVisitorNameIgnoreCaseOrderByCreatedAtDesc(String visitorName);
    List<TripHistory> findByCityIgnoreCaseOrderByCreatedAtDesc(String city);
    List<TripHistory> findAllByOrderByCreatedAtDesc();
}
