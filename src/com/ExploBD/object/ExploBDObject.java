package com.ExploBD.object;

import java.util.Date;

public abstract class ExploBDObject {

    protected String id;
    protected String name;
    protected Date createdAt;

    protected ExploBDObject(String id, String name) {
        this.id = id;
        this.name = name;
        this.createdAt = new Date();
    }

    public abstract String getObjectType();

    public String getId() {
        return id;
    }
    
    
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public final String getBasicInfo() {
        return name + " [" + getObjectType() + " #" + id + "]";
    }

    public void setName(String name) {
        this.name = name;
    }
}