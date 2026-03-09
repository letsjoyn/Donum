package com.ngo.util;

import com.ngo.dao.InventoryDAO;
import com.ngo.dao.RequirementDAO;
import com.ngo.model.InventoryItem;
import com.ngo.model.Requirement;

import java.util.*;

public class MatchingAlgorithm {

    /**
     * Priority-weighted matching algorithm.
     * Scores each requirement based on urgency, fulfillment gap, and inventory availability.
     * Returns an ordered list of distribution suggestions with priority scores.
     */
    public static List<Map<String, Object>> generateDistributionPlan() {
        RequirementDAO rDao = new RequirementDAO();
        InventoryDAO iDao = new InventoryDAO();

        List<Requirement> reqs = rDao.getPendingRequirements();
        List<InventoryItem> inv = iDao.getAllInventory();

        // Aggregate inventory across warehouses
        Map<String, Double> stockMap = new LinkedHashMap<>();
        for (InventoryItem item : inv) {
            stockMap.merge(item.getItemName().toLowerCase(), item.getQuantity(), Double::sum);
        }

        List<Map<String, Object>> plan = new ArrayList<>();

        for (Requirement r : reqs) {
            double remaining = r.getQuantityNeeded() - r.getQuantityFulfilled();
            if (remaining <= 0) continue;

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("requirementId", r.getRequirementId());
            entry.put("location", r.getLocation());
            entry.put("itemName", r.getItemName());
            entry.put("urgency", r.getUrgency());
            entry.put("quantityNeeded", remaining);
            entry.put("campaignName", r.getCampaignName());

            double available = stockMap.getOrDefault(r.getItemName().toLowerCase(), 0.0);
            entry.put("availableStock", available);

            // Priority score calculation (higher = more urgent)
            int score = getUrgencyScore(r.getUrgency());
            double gapRatio = remaining / Math.max(r.getQuantityNeeded(), 1);
            score += (int)(gapRatio * 30); // Gap weight: 0-30 points

            if (available >= remaining) {
                entry.put("matchType", "FULL");
                entry.put("canFulfill", remaining);
                entry.put("suggestion", "Can fully fulfill " + remaining + " " + r.getItemName());
            } else if (available > 0) {
                entry.put("matchType", "PARTIAL");
                entry.put("canFulfill", available);
                entry.put("suggestion", "Can provide " + available + " of " + remaining + " " + r.getItemName());
                score += 10; // Partial matches need attention
            } else {
                entry.put("matchType", "NONE");
                entry.put("canFulfill", 0.0);
                entry.put("suggestion", "No stock available for " + r.getItemName());
                score += 20; // No stock = highest priority to procure
            }

            entry.put("priorityScore", score);
            plan.add(entry);
        }

        // Sort by priority score descending
        plan.sort((a, b) -> Integer.compare((int) b.get("priorityScore"), (int) a.get("priorityScore")));
        return plan;
    }

    private static int getUrgencyScore(String urgency) {
        switch (urgency) {
            case "Critical": return 40;
            case "High":     return 30;
            case "Medium":   return 15;
            case "Low":      return 5;
            default:         return 10;
        }
    }
}
