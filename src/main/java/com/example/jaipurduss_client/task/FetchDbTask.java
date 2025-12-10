package com.example.jaipurduss_client.task;

import javafx.concurrent.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.example.jaipurduss_client.CommonUtils.AppConstant.baseUrl;
import static com.example.jaipurduss_client.CommonUtils.AppConstant.endPoints.societyDb;
import static com.example.jaipurduss_client.JaipurdussClientApplication.context;

public class FetchDbTask extends Task<byte[]> {
    private final String societyCode;
    private final String mobileNo;

    public FetchDbTask(String societyCode, String mobileNo) {
        this.mobileNo = mobileNo;
        this.societyCode = societyCode;
    }

    @Override
    protected byte[] call() {
        try {
            RestTemplate restTemplate = context.getBean(RestTemplate.class);
            String url = baseUrl + societyDb + String.format("?societyCode=%s&mobileNo=%s", societyCode, mobileNo);
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
