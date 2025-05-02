package org.example;

public class Blacklist {
    private int blacklistId;
    private int userId;
    private String reason;

    // Default constructor
    public Blacklist() {}

    // All-args constructor
    public Blacklist(int blacklistId, int userId, String reason) {
        this.blacklistId = blacklistId;
        this.userId = userId;
        this.reason = reason;
    }

    // Getters and Setters
    public int getBlacklistId() {
        return blacklistId;
    }

    public void setBlacklistId(int blacklistId) {
        this.blacklistId = blacklistId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
