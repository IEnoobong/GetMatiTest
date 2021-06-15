package com.example.getmatitest;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.FileOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Slf4j
@Service
@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class GetMatiHttpClient {

    private final OAuth2RestTemplate restTemplate;

    public JsonNode getResourceData(String url) {
        return restTemplate.getForObject(url, JsonNode.class);
    }

    public File downloadResource(String url) {
        return restTemplate.execute(url, HttpMethod.GET, null, clientHttpResponse -> {
            File ret = File.createTempFile(RandomStringUtils.randomAlphabetic(11), ".jpeg");
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
            return ret;
        });
    }
}
