package com.kaushik.travelplan.service.singletrip;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@Service
public class ImageService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String[] fallbackImages = {
        "https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&q=80&w=800",
        "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&q=80&w=800",
        "https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?auto=format&fit=crop&q=80&w=800",
        "https://images.unsplash.com/photo-1558222378-5a7ecb9308be?auto=format&fit=crop&q=80&w=800",
        "https://images.unsplash.com/photo-1477587458883-47145ed94245?auto=format&fit=crop&q=80&w=800"
    };

    public String fetchImageForLocation(String locationName) {
        if (locationName == null || locationName.trim().isEmpty()) {
            return getRandomFallback();
        }
        try {
            String encoded = java.net.URLEncoder.encode(locationName, "UTF-8");
            // First search wikipedia for the title
            String searchUrl = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=" 
                               + encoded + "&utf8=&format=json&srlimit=1";
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "TravelMindApp/1.0 (contact: support@travelmind.app)");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            System.out.println("Searching Wikipedia for: " + locationName);
            ResponseEntity<Map<String, Object>> searchRes = restTemplate.exchange(searchUrl, org.springframework.http.HttpMethod.GET, entity, (Class<Map<String, Object>>)(Class)Map.class);
            
            if (searchRes.getStatusCode().is2xxSuccessful() && searchRes.getBody() != null) {
                Map<String, Object> query = (Map<String, Object>) searchRes.getBody().get("query");
                if (query != null) {
                    java.util.List<Map<String, Object>> search = (java.util.List<Map<String, Object>>) query.get("search");
                    if (search != null && !search.isEmpty()) {
                        String title = (String) search.get(0).get("title");
                        String encodedTitle = java.net.URLEncoder.encode(title.replace(" ", "_"), "UTF-8");
                        
                        // Fetch the page summary for the exact title
                        String summaryUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/" + encodedTitle;
                        System.out.println("Fetching summary for: " + title);
                        ResponseEntity<Map<String, Object>> summaryRes = restTemplate.exchange(summaryUrl, org.springframework.http.HttpMethod.GET, entity, (Class<Map<String, Object>>)(Class)Map.class);
                        
                        if (summaryRes.getStatusCode().is2xxSuccessful() && summaryRes.getBody() != null) {
                            Map<String, Object> body = summaryRes.getBody();
                            if (body.containsKey("originalimage")) {
                                Map<String, Object> original = (Map<String, Object>) body.get("originalimage");
                                String url = (String) original.get("source");
                                System.out.println("Found image: " + url);
                                return url;
                            } else if (body.containsKey("thumbnail")) {
                                Map<String, Object> thumb = (Map<String, Object>) body.get("thumbnail");
                                return (String) thumb.get("source");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching image for " + locationName + ": " + e.getMessage());
        }
        
        return getRandomFallback();
    }
    
    private String getRandomFallback() {
        return fallbackImages[(int)(Math.random() * fallbackImages.length)];
    }
}

