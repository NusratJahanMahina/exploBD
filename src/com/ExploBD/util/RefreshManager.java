package com.ExploBD.util;

import java.util.ArrayList;
import java.util.List;


public class RefreshManager {
    
    private static RefreshManager instance;
    private List<RefreshListener> listeners = new ArrayList<>();
    
    public interface RefreshListener {
        void onRefresh();
    }
    
    private RefreshManager() {}
    
    public static RefreshManager getInstance() {
        if (instance == null) {
            instance = new RefreshManager();
        }
        return instance;
    }
    
    public void register(RefreshListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void unregister(RefreshListener listener) {
        listeners.remove(listener);
    }
    
    public void refreshAll() {
        System.out.println("RefreshManager: Notifying " + listeners.size() + " listeners");
        for (RefreshListener listener : listeners) {
            listener.onRefresh();
        }
    }
}