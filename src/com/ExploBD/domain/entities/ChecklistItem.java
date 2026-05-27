package com.ExploBD.domain.entities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ChecklistItem<T> {
    private String id;
    private String title;
    private T target;
    private Map<String, Boolean> userProgress;
    private LocalDateTime createdAt;
    private String createdBy;
    private int completionCount; // Add this field
    
    public ChecklistItem(String id, String title, T target, String createdBy) {
        this.id = id;
        this.title = title;
        this.target = target;
        this.createdBy = createdBy;
        this.userProgress = new HashMap<>();
        this.createdAt = LocalDateTime.now();
        this.completionCount = 0;
    }
    
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public T getTarget() { return target; }
    
    public boolean isCompletedByUser(String userId) {
        return userProgress.getOrDefault(userId, false);
    }
    
    public void setUserCompleted(String userId, boolean completed) {
        userProgress.put(userId, completed);
    }
    
    public int getCompletionCount() { return completionCount; }
    public void setCompletionCount(int count) { this.completionCount = count; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
}