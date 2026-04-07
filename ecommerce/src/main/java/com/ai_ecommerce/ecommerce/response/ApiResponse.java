package com.ai_ecommerce.ecommerce.response;

public class ApiResponse {

    private String message;
    private Object data;
    private int status;

    public ApiResponse(String message, Object data, int status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }
}