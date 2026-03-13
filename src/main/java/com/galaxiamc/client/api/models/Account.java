package com.galaxiamc.client.api.models;

import com.google.gson.annotations.SerializedName;

public class Account {
    public String id;
    public String username;
    public String acct;
    @SerializedName("display_name")
    public String displayName;
    public boolean locked;
    public boolean bot;
    @SerializedName("created_at")
    public String createdAt;
    public String note;
    public String url;
    public String avatar;
    @SerializedName("avatar_static")
    public String avatarStatic;
    public String header;
    @SerializedName("header_static")
    public String headerStatic;
    @SerializedName("followers_count")
    public int followersCount;
    @SerializedName("following_count")
    public int followingCount;
    @SerializedName("statuses_count")
    public int statusesCount;
    
    public String getDisplayNameOrUsername() {
        return displayName != null && !displayName.isEmpty() ? displayName : username;
    }
}
