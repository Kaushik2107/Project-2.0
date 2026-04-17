package com.kaushik.travelplan.controller;

import com.kaushik.travelplan.entity.TripHistory;
import com.kaushik.travelplan.service.singletrip.TripHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/history")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class TripHistoryController {

    @Autowired
    private TripHistoryService historyService;

    @GetMapping
    public List<TripHistory> getMyHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return historyService.getHistoryByVisitor(username);
    }

    @DeleteMapping("/{id}")
    public void deleteHistory(@PathVariable String id) {
        // Technically should verify ownership before deleting
        historyService.deleteHistory(id);
    }
}

