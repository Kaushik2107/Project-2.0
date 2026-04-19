package com.kaushik.travelplan.service.singletrip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaushik.travelplan.dto.AiRecommendation;
import com.kaushik.travelplan.dto.TripResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LLMService {

    @Value("${openrouter.api.key:}")
    private String apiKey;

    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiRecommendation getRecommendation(String city, int days, int targetBudget, List<TripResponse> plans) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                return getDynamicFallback(city, days, targetBudget, plans);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek/deepseek-chat");

            String prompt = createRecommendationPrompt(city, days, targetBudget, plans);
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    
                    String json = extractJson(content);
                    return objectMapper.readValue(json, AiRecommendation.class);
                }
            }
        } catch (Exception e) {
            System.err.println("LLM Recommendation Error: " + e.getMessage());
        }
        
        return getDynamicFallback(city, days, targetBudget, plans);
    }

    private String extractJson(String content) {
        // Robust extraction using regex for blocks or the whole string
        Pattern pattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group();
        }
        return content.trim();
    }

    private AiRecommendation getDynamicFallback(String city, int days, int targetBudget, List<TripResponse> plans) {
        // Find the best tier based on score vs budget
        TripResponse best = plans.get(1); // Default to standard
        String tier = "Standard";
        
        if (plans.size() >= 3) {
            TripResponse elite = plans.get(2);
            TripResponse standard = plans.get(1);
            
            // Safety check for null scores
            int eliteScore = (elite.getTripScore() != null) ? elite.getTripScore().getOverallScore() : 0;
            int standardScore = (standard.getTripScore() != null) ? standard.getTripScore().getOverallScore() : 0;
            
            // If elite has much higher score and is close to budget, suggest it
            if (eliteScore > standardScore + 10 && elite.getTotalCost() <= targetBudget * 1.25) {
                best = elite;
                tier = "Elite";
            }
        }

        int bestScore = (best.getTripScore() != null) ? best.getTripScore().getOverallScore() : 0;

        AiRecommendation fallback = new AiRecommendation();
        fallback.setBestTierName(tier);
        fallback.setPersuasiveHeadline("The " + tier + " Plan is statistically your best value for " + city);
        fallback.setQuantitativeJustifications(List.of(
            "Achieves a quality score of " + bestScore + "/100 based on your preferences",
            "Optimizes for " + (best.getPlaces() != null ? best.getPlaces().size() : 0) + " curated experiences",
            "Delivers the highest 'Joy-per-Rupee' ratio for a " + days + "-day stay"
        ));
        fallback.setDecisionLogic("After analyzing your ₹" + targetBudget + " target, the " + tier + " tier emerges as the winner. It avoids the compromises of basic plans while staying well within the range of diminishing returns.");
        fallback.setSmartTip("Private transport will maintain your " + bestScore + "% efficiency rating!");
        return fallback;
    }

    private String createRecommendationPrompt(String city, int days, int targetBudget, List<TripResponse> plans) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a Persuasive Travel Consultant. Analyze these 3 travel options for ").append(city)
          .append(" for ").append(days).append(" days, considering the user's TARGET BUDGET of ₹").append(targetBudget).append(".")
          .append("\nYou MUST pick ONE winner. Compare the 'TripScore' (Quality) vs the 'TotalCost' (Price).")
          .append("\nReturn EXACTLY this JSON structure:")
          .append("\n{")
          .append("\n  \"best_tier_name\": \"Economic, Standard, or Elite\",")
          .append("\n  \"persuasive_headline\": \"A bold headline naming the best choice\",")
          .append("\n  \"quantitative_justifications\": [")
          .append("\n    \"Justification with a HARD NUMBER comparing Score vs Cost (e.g., 'Spending 15% more for Elite gains a 40% jump in Quality Score')\",")
          .append("\n    \"Justification with a HARD NUMBER (e.g., 'Economic keeps 85% of attractions while saving ₹6,000')\",")
          .append("\n    \"Justification with a HARD NUMBER (e.g., 'Standard is the only tier maintaining a Score > 90 within budget')\"")
          .append("\n  ],")
          .append("\n  \"decision_logic\": \"A highly persuasive paragraph explaining the logic. Focus on the value of the TripScore vs the User's Target Budget.\",")
          .append("\n  \"smart_tip\": \"A friendly tip to make the best tier even better\"")
          .append("\n}")
          .append("\n\nComparison Data (Target Budget: ₹").append(targetBudget).append("):");
        
        for (int i = 0; i < plans.size(); i++) {
            TripResponse p = plans.get(i);
            String tier = (i == 0) ? "Economic" : (i == 1) ? "Standard" : "Elite";
            int score = (p.getTripScore() != null) ? p.getTripScore().getOverallScore() : 0;
            sb.append("\n- ").append(tier).append(" Tier: Cost ₹").append(p.getTotalCost())
              .append(", TripScore: ").append(score).append("/100")
              .append(", Stay: ").append(p.getHotel() != null ? p.getHotel().getName() : "Standard")
              .append(", Attraction Count: ").append(p.getPlaces() != null ? p.getPlaces().size() : 0);
        }
        return sb.toString();
    }
}
