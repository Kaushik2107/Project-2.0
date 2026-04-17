package com.kaushik.travelplan.controller;

import com.kaushik.travelplan.dto.ReviewRequest;
import com.kaushik.travelplan.entity.Review;
import com.kaushik.travelplan.service.singletrip.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public Review submitReview(@RequestBody ReviewRequest req) {
        return reviewService.submitReview(req);
    }

    @GetMapping("/{entityId}")
    public Map<String, Object> getReviews(@PathVariable String entityId) {
        Map<String, Object> result = new HashMap<>();
        result.put("reviews", reviewService.getReviewsForEntity(entityId));
        result.put("averageRating", reviewService.getAverageRating(entityId));
        result.put("totalReviews", reviewService.getReviewCount(entityId));
        return result;
    }
}

