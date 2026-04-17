package com.kaushik.travelplan.service.singletrip;

import com.kaushik.travelplan.dto.ReviewRequest;
import com.kaushik.travelplan.entity.Review;
import com.kaushik.travelplan.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * FEATURE 19: Review & Rating Service
 * Users can rate hotels, places, and restaurants.
 */
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;

    /**
     * Submit a new review.
     */
    public Review submitReview(ReviewRequest req) {
        Review review = new Review();
        review.setEntityId(req.getEntityId());
        review.setEntityType(req.getEntityType());
        review.setVisitorName(req.getVisitorName() != null ? req.getVisitorName() : "Anonymous");
        review.setRating(Math.min(5.0, Math.max(1.0, req.getRating())));
        review.setComment(req.getComment());
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepo.save(review);
    }

    /**
     * Get all reviews for a specific entity (hotel/place/restaurant).
     */
    public List<Review> getReviewsForEntity(String entityId) {
        return reviewRepo.findByEntityIdOrderByCreatedAtDesc(entityId);
    }

    /**
     * Get average rating for an entity.
     */
    public double getAverageRating(String entityId) {
        List<Review> reviews = reviewRepo.findByEntityIdOrderByCreatedAtDesc(entityId);
        if (reviews.isEmpty()) return 0;
        return reviews.stream().mapToDouble(Review::getRating).average().orElse(0);
    }

    /**
     * Get review count for an entity.
     */
    public int getReviewCount(String entityId) {
        return reviewRepo.findByEntityIdOrderByCreatedAtDesc(entityId).size();
    }
}

