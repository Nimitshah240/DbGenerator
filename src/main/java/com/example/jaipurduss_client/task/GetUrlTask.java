package com.example.jaipurduss_client.task;

import com.example.jaipurduss_client.dto.RequestDto;
import com.example.jaipurduss_client.dto.ResponseDto;
import javafx.concurrent.Task;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.example.jaipurduss_client.JaipurdussClientApplication.context;

public class GetUrlTask extends Task<String> {
    private final String clientCode;

    public GetUrlTask(String clientCode) {
        this.clientCode = clientCode;
    }
    String url = "http://192.168.1.41:6375/eipl-amcs-processor/";

    @Override
    protected String call() throws Exception {
        try {
            RestTemplate restTemplate = context.getBean(RestTemplate.class);
            String identityUrl = "http://amcsapp.emilkpro.in/webservice/eipl/v1/eipl-app/verify-identity";
            RequestDto payload = new RequestDto("","",clientCode,"", "","");
            ResponseEntity<ResponseDto> response = restTemplate.exchange(identityUrl, HttpMethod.POST, new HttpEntity<>(payload), ResponseDto.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ResponseDto respBody = response.getBody();
                Map<String, Object> dataMap = respBody.getData();
                String vendorUrl = (String) dataMap.get("vendorUrl");
                if (vendorUrl != null) {
                    String[] partsUrl = vendorUrl.split("#");
                    url = partsUrl[2];
                }
                return url;
            }
            return null;
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }
}