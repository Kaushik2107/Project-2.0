package com.kaushik.travelplan.util;

import java.time.LocalDate;
import java.time.Month;

/**
 * Utility class for determining seasonal pricing multipliers.
 * Peak season = higher prices, Off-peak = discounted prices.
 */
public class SeasonUtil {

    private SeasonUtil() {}

    public enum Season {
        PEAK,       // Dec-Jan, May-Jun (holidays, summer vacation)
        MODERATE,   // Feb-Mar, Oct-Nov (pleasant weather)
        OFF_PEAK    // Apr, Jul-Sep (extreme heat, monsoon)
    }

    /**
     * Determine the season for a given date.
     */
    public static Season getSeason(LocalDate date) {
        Month month = date.getMonth();
        switch (month) {
            case DECEMBER:
            case JANUARY:
            case MAY:
            case JUNE:
                return Season.PEAK;
            case FEBRUARY:
            case MARCH:
            case OCTOBER:
            case NOVEMBER:
                return Season.MODERATE;
            default:
                return Season.OFF_PEAK;
        }
    }

    /**
     * Get price multiplier for the given date.
     * PEAK = 1.3x, MODERATE = 1.0x, OFF_PEAK = 0.8x
     */
    public static double getPriceMultiplier(LocalDate date) {
        switch (getSeason(date)) {
            case PEAK:     return 1.30;
            case OFF_PEAK: return 0.80;
            default:       return 1.00;
        }
    }

    /**
     * Get the season label for display purposes.
     */
    public static String getSeasonLabel(LocalDate date) {
        switch (getSeason(date)) {
            case PEAK:     return "🔥 Peak Season (prices +30%)";
            case OFF_PEAK: return "💰 Off-Peak Season (prices -20%)";
            default:       return "🌤️ Moderate Season (normal prices)";
        }
    }
}
