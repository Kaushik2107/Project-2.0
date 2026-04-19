/**
 * SIMULATION TEST: LLM Prompt Accuracy Verification
 * 
 * This script dry-runs the prompt generation logic to verify that the
 * AI Consultant is receiving the correct mathematical data to make 
 * 'perfect' decisions.
 */

public class LlmSimulationTest {

    public static void runSimulation() {
        System.out.println("=== LLM PERFECT ACCURACY TEST SUITE ===\n");

        // Scenario 1: The 'Bargain' Hunt
        // User wants luxury but budget is tight.
        testScenario("Goa", 4, 15000, 
            65, 12000, // Eco: Low score, low price
            85, 14500, // Main: High score, exactly on budget
            92, 22000  // Elite: Slightly better score, but way over budget
        );

        // Scenario 2: The 'Value' Play
        // Spending 10% more gets 40% better quality.
        testScenario("Manali", 5, 20000, 
            40, 14000, // Eco
            60, 20000, // Main: Standard quality
            95, 22500  // Elite: HUGE quality jump for small price increase
        );
    }

    private static void testScenario(String city, int days, int target, 
                                   int s1, int c1, 
                                   int s2, int c2, 
                                   int s3, int c3) {
        
        System.out.println("SCENARIO: " + city + " (" + days + " days) | Target: ₹" + target);
        System.out.println("--------------------------------------------------");
        
        StringBuilder sb = new StringBuilder();
        sb.append("Analysis Context:\n");
        sb.append("- Eco: ₹").append(c1).append(" | Score: ").append(s1).append("/100\n");
        sb.append("- Main: ₹").append(c2).append(" | Score: ").append(s2).append("/100\n");
        sb.append("- Elite: ₹").append(c3).append(" | Score: ").append(s3).append("/100\n");

        // Simulate Dynamic Fallback Pick
        String pick = "Standard";
        if (s3 > s2 + 10 && c3 <= target * 1.25) pick = "Elite";
        if (s2 < 50 && s3 > 80) pick = "Elite";
        
        System.out.println("AI Verdict Prediction: " + pick);
        System.out.println("Logical Reasoning: \"Based on your ₹" + target + " target, " + 
                           (pick.equals("Elite") ? "the Elite plan offers a significant " + (s3-s2) + " point jump in quality for only ₹" + (c3-c2) + " extra." : "the Standard plan is the most efficient choice.\"\n"));
    }

    public static void main(String[] args) {
        runSimulation();
    }
}
