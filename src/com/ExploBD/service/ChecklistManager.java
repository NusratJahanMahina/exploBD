package com.ExploBD.service;

import com.ExploBD.data.databaseObject.ChecklistDatabaseObject;
import com.ExploBD.domain.entities.ChecklistItem;
import com.ExploBD.object.User;
import java.util.*;

public class ChecklistManager<T> {
    private Map<String, ChecklistItem<T>> items;
    private List<String> orderedItemIds;
    private User currentUser;
    private boolean isLeaderMode;
    private String tourId;
    private ChecklistDatabaseObject db;
    
    public ChecklistManager(String tourId, User currentUser, boolean isLeaderMode) {
        this.tourId = tourId;
        this.currentUser = currentUser;
        this.isLeaderMode = isLeaderMode;
        this.items = new LinkedHashMap<>();
        this.orderedItemIds = new ArrayList<>();
        this.db = new ChecklistDatabaseObject();
        
        // Load existing data from database
        loadFromDatabase();
    }
    
    @SuppressWarnings("unchecked")
    private void loadFromDatabase() {
        // Clear existing items first
        items.clear();
        orderedItemIds.clear();
        
        // Load all checklist items with progress
        List<Map<String, Object>> dbItems = db.getChecklistWithProgress(tourId, currentUser.getUserId());
        
        for (Map<String, Object> dbItem : dbItems) {
            int dbId = (int) dbItem.get("id");
            String itemText = (String) dbItem.get("item_text");
            boolean completed = (boolean) dbItem.get("completed");
            int completionCount = (int) dbItem.get("completion_count");
            
            // Create item ID (using database ID as string)
            String itemId = String.valueOf(dbId);
            
            // Create ChecklistItem
            ChecklistItem<T> item = new ChecklistItem<>(itemId, itemText, null, (String) dbItem.get("created_by"));
            
            // Set completion status for current user
            item.setUserCompleted(currentUser.getUserId(), completed);
            
            // Store completion count for display
            item.setCompletionCount(completionCount);
            
            items.put(itemId, item);
            orderedItemIds.add(itemId);
        }
    }
    
    public boolean addItem(String id, String title, T target) {
        if (!isLeaderMode) return false;
        
        // Save to database
        boolean success = db.addItem(tourId, title, currentUser.getUserId());
        
        if (success) {
            // Reload ALL items from database (this will clear and reload everything)
            loadFromDatabase();
            return true;
        }
        return false;
    }
    
    public boolean removeItem(String itemId) {
        if (!isLeaderMode) return false;
        
        int dbId = Integer.parseInt(itemId);
        boolean success = db.deleteItem(dbId);
        
        if (success) {
            // Remove from local maps
            items.remove(itemId);
            orderedItemIds.remove(itemId);
            return true;
        }
        return false;
    }
    
    public boolean updateItemTitle(String itemId, String newTitle) {
        if (!isLeaderMode) return false;
        
        int dbId = Integer.parseInt(itemId);
        boolean success = db.updateItem(dbId, newTitle);
        
        if (success) {
            ChecklistItem<T> item = items.get(itemId);
            if (item != null) {
                item.setTitle(newTitle);
            }
            return true;
        }
        return false;
    }
    
    public boolean toggleCompletion(String itemId) {
        int dbId = Integer.parseInt(itemId);
        
        // Toggle in database
        boolean success = db.toggleCompletion(dbId, currentUser.getUserId());
        
        if (success) {
            // Update local item
            ChecklistItem<T> item = items.get(itemId);
            if (item != null) {
                boolean currentStatus = item.isCompletedByUser(currentUser.getUserId());
                item.setUserCompleted(currentUser.getUserId(), !currentStatus);
            }
            return true;
        }
        return false;
    }
    
    public List<ChecklistItem<T>> getAllItems() {
        List<ChecklistItem<T>> result = new ArrayList<>();
        for (String id : orderedItemIds) {
            result.add(items.get(id));
        }
        return result;
    }
    
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        int total = items.size();
        int userCompleted = 0;
        
        for (ChecklistItem<T> item : items.values()) {
            if (item.isCompletedByUser(currentUser.getUserId())) {
                userCompleted++;
            }
        }
        
        stats.put("total", total);
        stats.put("userCompleted", userCompleted);
        stats.put("userProgress", total > 0 ? (userCompleted * 100 / total) : 0);
        
        return stats;
    }
    
    public int getCompletionCount(String itemId) {
        ChecklistItem<T> item = items.get(itemId);
        if (item != null) {
            return item.getCompletionCount();
        }
        return 0;
    }
    
    public int size() { return items.size(); }
    public boolean isEmpty() { return items.isEmpty(); }
}