package com.example.getmatitest;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestRunner {

    private final GetMatiHttpClient getMatiHttpClient;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            //replace url with resource from your webhook
            JsonNode resourceData = getMatiHttpClient.getResourceData("REPLACE_WITH_YOUR_WEBHOOK_RESOURCE_URL");
            log.info("RESOURCE_DATA {}", resourceData);

            File file = getMatiHttpClient.downloadResource(resourceData.withArray("steps").get(0).get("data").get("selfiePhotoUrl").asText());
            log.info("File is at {}", file.getAbsolutePath());
        };
    }
}
