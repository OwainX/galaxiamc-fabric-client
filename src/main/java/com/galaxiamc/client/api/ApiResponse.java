package com.galaxiamc.client.api;

import java.util.Optional;

public class ApiResponse<T> {
    private final T data;
    private final String error;
    private final int statusCode;

    private ApiResponse(T data, String error, int statusCode) {
        this.data = data;
        this.error = error;
        this.statusCode = statusCode;
    }

    public static <T> ApiResponse<T> success(T data, int statusCode) {
        return new ApiResponse<>(data, null, statusCode);
    }

    public static <T> ApiResponse<T> error(String error, int statusCode) {
        return new ApiResponse<>(null, error, statusCode);
    }

    public boolean isSuccess() {
        return error == null && data != null;
    }

    public Optional<T> getData() {
        return Optional.ofNullable(data);
    }

    public String getError() {
        return error;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
