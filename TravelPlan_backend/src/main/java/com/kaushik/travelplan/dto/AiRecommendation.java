package com.kaushik.travelplan.dto;

import java.util.List;

public class AiRecommendation {
    private String bestTierName;
    private String persuasiveHeadline;
    private List<String> quantitativeJustifications;
    private String decisionLogic;
    private String smartTip;

    // Getters and Setters
    public String getBestTierName() { return bestTierName; }
    public void setBestTierName(String bestTierName) { this.bestTierName = bestTierName; }

    public String getPersuasiveHeadline() { return persuasiveHeadline; }
    public void setPersuasiveHeadline(String persuasiveHeadline) { this.persuasiveHeadline = persuasiveHeadline; }

    public List<String> getQuantitativeJustifications() { return quantitativeJustifications; }
    public void setQuantitativeJustifications(List<String> quantitativeJustifications) { this.quantitativeJustifications = quantitativeJustifications; }

    public String getDecisionLogic() { return decisionLogic; }
    public void setDecisionLogic(String decisionLogic) { this.decisionLogic = decisionLogic; }

    public String getSmartTip() { return smartTip; }
    public void setSmartTip(String smartTip) { this.smartTip = smartTip; }
}
