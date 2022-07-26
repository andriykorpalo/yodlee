package com.strabo.model;

public class ResponseError {
    private int statusCode;
    private String message;

    public ResponseError() {

    }

    public ResponseError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

}
