package com.galaxiamc.client.api.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Status {
    public String id;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("in_reply_to_id")
    public String inReplyToId;
    @SerializedName("in_reply_to_account_id")
    public String inReplyToAccountId;
    public boolean sensitive;
    @SerializedName("spoiler_text")
    public String spoilerText;
    public String visibility;
    public String language;
    public String uri;
    public String url;
    @SerializedName("replies_count")
    public int repliesCount;
    @SerializedName("reblogs_count")
    public int reblogsCount;
    @SerializedName("favourites_count")
    public int favouritesCount;
    public boolean favourited;
    public boolean reblogged;
    public boolean muted;
    public boolean bookmarked;
    public String content;
    public Status reblog;
    public Account account;
    @SerializedName("media_attachments")
    public List<MediaAttachment> mediaAttachments;
    public List<Mention> mentions;
    public List<Tag> tags;
    
    public String getPlainContent() {
        if (content == null) return "";
        return content.replaceAll("<[^>]*>", "");
    }
    
    public static class MediaAttachment {
        public String id;
        public String type;
        public String url;
        @SerializedName("preview_url")
        public String previewUrl;
        public String description;
    }
    
    public static class Mention {
        public String id;
        public String username;
        public String url;
        public String acct;
    }
    
    public static class Tag {
        public String name;
        public String url;
    }
}
