package com.example.jaipurduss_client.task;

import javafx.concurrent.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.example.jaipurduss_client.CommonUtils.AppConstant.endPoints.societyDb;
import static com.example.jaipurduss_client.JaipurdussClientApplication.context;

public class FetchDbTask extends Task<byte[]> {
    private final String societyCode;
    private final String mobileNo;
    private final String url;

    public FetchDbTask(String societyCode, String mobileNo, String url) {
        this.mobileNo = mobileNo;
        this.societyCode = societyCode;
        this.url = url;
    }

    @Override
    protected byte[] call() {
        try {
            RestTemplate restTemplate = context.getBean(RestTemplate.class);
            String urlStr = url + societyDb + String.format("?societyCode=%s&mobileNo=%s", societyCode, mobileNo);
            ResponseEntity<byte[]> response = restTemplate.getForEntity(urlStr, byte[].class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
            return null;
        } catch (Exception e) {
            if (e.getMessage().contains("Connection refused"))
                System.out.println("Connection refused");
            throw new RuntimeException(e);
        }
    }
}
