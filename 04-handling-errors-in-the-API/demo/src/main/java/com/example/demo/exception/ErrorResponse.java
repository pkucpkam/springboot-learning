package com.example.demo.exception;

public class ErrorResponse {
    private int status;
    private String message;

    public ErrorResponse(int value, String message) {
        this.status = value;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }

}
