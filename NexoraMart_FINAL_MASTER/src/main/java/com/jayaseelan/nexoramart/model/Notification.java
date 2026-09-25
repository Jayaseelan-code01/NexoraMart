package com.jayaseelan.nexoramart.model;

import java.sql.Timestamp;

public class Notification {
    private String id;
    private String type;
    private String title;
    private String message;
    private String link;
    private Timestamp createdAt;

    public Notification() {}
    public Notification(String id, String type, String title, String message, String link, Timestamp createdAt) {
        this.id = id; this.type = type; this.title = title; this.message = message; this.link = link; this.createdAt = createdAt;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
