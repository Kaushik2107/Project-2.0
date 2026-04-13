package com.kaushik.travelplan.repository;

import com.kaushik.travelplan.entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByEntityIdOrderByCreatedAtDesc(String entityId);
    List<Review> findByEntityTypeOrderByCreatedAtDesc(String entityType);
    List<Review> findByEntityIdAndEntityType(String entityId, String entityType);
}
