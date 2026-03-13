package com.galaxiamc.client.api.models;

import com.google.gson.annotations.SerializedName;

public class Notification {
    public String id;
    public String type;
    @SerializedName("created_at")
    public String createdAt;
    public Account account;
    public Status status;
    
    public enum Type {
        MENTION,
        REBLOG,
        FAVOURITE,
        FOLLOW,
        POLL,
        FOLLOW_REQUEST
    }
    
    public Type getTypeEnum() {
        try {
            return Type.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Type.MENTION;
        }
    }
    
    public String getIcon() {
        return switch (getTypeEnum()) {
            case FAVOURITE -> "❤️";
            case REBLOG -> "🔁";
            case FOLLOW -> "👤";
            case MENTION -> "💬";
            case POLL -> "📊";
            case FOLLOW_REQUEST -> "👥";
        };
    }
}
