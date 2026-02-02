package com.example.jaipurduss_client.dto;

import java.util.List;
import java.util.Map;

public class ResponseDto {

    private int statusCode;
    private List<String> message;
    private Map<String, Object> data;

    public ResponseDto(){

    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}