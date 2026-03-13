package com.galaxiamc.client.api;

import com.galaxiamc.client.api.models.Account;
import com.galaxiamc.client.api.models.Notification;
import com.galaxiamc.client.api.models.Status;
import com.galaxiamc.client.config.GalaxiaConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ApiClient {
    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    
    private static ApiClient instance;

    private final GalaxiaConfig config;

    public ApiClient(GalaxiaConfig config) {
        this.config = config;
    }

    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient(GalaxiaConfig.getInstance());
        }
        return instance;
    }

    public CompletableFuture<ApiResponse<List<Status>>> getHomeFeed(String maxId) {
        String url = buildUrl("/api/v1/timelines/home", Map.of("max_id", maxId, "limit", String.valueOf(config.getPageSize())));
        return getRequest(url, new TypeToken<List<Status>>() {});
    }

    public CompletableFuture<ApiResponse<List<Status>>> getPublicFeed(String maxId) {
        String url = buildUrl("/api/v1/timelines/public", Map.of("max_id", maxId, "limit", String.valueOf(config.getPageSize())));
        return getRequest(url, new TypeToken<List<Status>>() {});
    }

    public CompletableFuture<ApiResponse<Account>> getProfile(String accountId) {
        String url = buildUrl("/api/v1/accounts/" + accountId, Map.of());
        return getRequest(url, new TypeToken<Account>() {});
    }

    public CompletableFuture<ApiResponse<List<Status>>> getAccountStatuses(String accountId, String maxId) {
        String url = buildUrl("/api/v1/accounts/" + accountId + "/statuses", Map.of("max_id", maxId, "limit", String.valueOf(config.getPageSize())));
        return getRequest(url, new TypeToken<List<Status>>() {});
    }

    public CompletableFuture<ApiResponse<Status>> getStatus(String statusId) {
        String url = buildUrl("/api/v1/statuses/" + statusId, Map.of());
        return getRequest(url, new TypeToken<Status>() {});
    }

    public CompletableFuture<ApiResponse<List<Notification>>> getNotifications() {
        String url = buildUrl("/api/v1/notifications", Map.of("limit", String.valueOf(config.getPageSize())));
        return getRequest(url, new TypeToken<List<Notification>>() {});
    }

    public CompletableFuture<ApiResponse<Status>> postStatus(String content, String replyToId) {
        String url = buildUrl("/api/v1/statuses", Map.of());
        Map<String, String> body = new HashMap<>();
        body.put("status", content);
        if (replyToId != null && !replyToId.isEmpty()) {
            body.put("in_reply_to_id", replyToId);
        }
        return postRequest(url, body, new TypeToken<Status>() {});
    }

    public CompletableFuture<ApiResponse<Status>> favouriteStatus(String statusId) {
        String url = buildUrl("/api/v1/statuses/" + statusId + "/favourite", Map.of());
        return postRequest(url, Map.of(), new TypeToken<Status>() {});
    }

    public CompletableFuture<ApiResponse<Status>> unfavouriteStatus(String statusId) {
        String url = buildUrl("/api/v1/statuses/" + statusId + "/unfavourite", Map.of());
        return postRequest(url, Map.of(), new TypeToken<Status>() {});
    }

    public CompletableFuture<ApiResponse<Status>> reblogStatus(String statusId) {
        String url = buildUrl("/api/v1/statuses/" + statusId + "/reblog", Map.of());
        return postRequest(url, Map.of(), new TypeToken<Status>() {});
    }

    public CompletableFuture<ApiResponse<Status>> unreblogStatus(String statusId) {
        String url = buildUrl("/api/v1/statuses/" + statusId + "/unreblog", Map.of());
        return postRequest(url, Map.of(), new TypeToken<Status>() {});
    }

    public CompletableFuture<ApiResponse<List<Account>>> searchAccounts(String query) {
        String url = buildUrl("/api/v1/accounts/search", Map.of("q", query, "limit", "10"));
        return getRequest(url, new TypeToken<List<Account>>() {});
    }

    public CompletableFuture<ApiResponse<String>> requestLinkCode() {
        String url = buildUrl("/api/v1/auth/minecraft/request_code", Map.of());
        return getRequest(url, new TypeToken<String>() {});
    }

    private String buildUrl(String path, Map<String, String> params) {
        StringBuilder url = new StringBuilder(config.getApiBaseUrl());
        url.append(path);
        
        if (!params.isEmpty()) {
            url.append("?");
            params.entrySet().stream()
                    .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                    .forEach(e -> url.append(e.getKey()).append("=").append(e.getValue()).append("&"));
            url.deleteCharAt(url.length() - 1);
        }
        
        return url.toString();
    }

    private <T> CompletableFuture<ApiResponse<T>> getRequest(String url, TypeToken<T> typeToken) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(10));

        if (config.isLinked()) {
            builder.header("Authorization", "Bearer " + config.getAccessToken());
        }

        HttpRequest request = builder.build();

        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        try {
                            T data = GSON.fromJson(response.body(), typeToken.getType());
                            return ApiResponse.<T>success(data, response.statusCode());
                        } catch (Exception e) {
                            return ApiResponse.<T>error("Failed to parse response: " + e.getMessage(), response.statusCode());
                        }
                    } else {
                        return ApiResponse.<T>error("HTTP " + response.statusCode() + ": " + response.body(), response.statusCode());
                    }
                })
                .exceptionally(e -> ApiResponse.<T>error("Request failed: " + e.getMessage(), 0));
    }

    private <T> CompletableFuture<ApiResponse<T>> postRequest(String url, Map<String, String> body, TypeToken<T> typeToken) {
        String jsonBody = GSON.toJson(body);
        
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10));

        if (config.isLinked()) {
            builder.header("Authorization", "Bearer " + config.getAccessToken());
        }

        HttpRequest request = builder.build();

        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        try {
                            T data = GSON.fromJson(response.body(), typeToken.getType());
                            return ApiResponse.<T>success(data, response.statusCode());
                        } catch (Exception e) {
                            return ApiResponse.<T>error("Failed to parse response: " + e.getMessage(), response.statusCode());
                        }
                    } else {
                        return ApiResponse.<T>error("HTTP " + response.statusCode() + ": " + response.body(), response.statusCode());
                    }
                })
                .exceptionally(e -> ApiResponse.<T>error("Request failed: " + e.getMessage(), 0));
    }
}
