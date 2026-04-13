package com.kaushik.travelplan.controller;

import com.kaushik.travelplan.dto.ComparisonRequest;
import com.kaushik.travelplan.dto.ComparisonResponse;
import com.kaushik.travelplan.service.ComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plan/compare")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "http://127.0.0.1:5173"})
public class ComparisonController {

    @Autowired
    private ComparisonService comparisonService;

    @PostMapping
    public ComparisonResponse compare(@RequestBody ComparisonRequest req) {
        return comparisonService.compareBudgets(req);
    }
}
