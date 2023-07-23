package com.opodoliako.starwarsapiimperial.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class PeopleController {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${swapi.base-url}")
    private String swapiBaseUrl;

    @GetMapping("/people/{id}")
    public JsonNode getPerson(@PathVariable int id) throws IOException {
        Request request = new Request.Builder()
                .url(swapiBaseUrl + "/people/" + id)
                .build();


        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Convert height to imperial and metric units
            JsonNode heightNode = jsonNode.get("height");
            int heightCm = heightNode.asInt();
            int heightFeet = (int) Math.floor(heightCm / 30.48);
            int heightInches = (int) Math.round((heightCm % 30.48) / 2.54);
            ((ObjectNode) jsonNode).put("height", heightFeet + " feet " + heightInches + " inches");

            // Convert weight to imperial and metric units
            JsonNode weightNode = jsonNode.get("mass");
            String weightKgString = weightNode.asText().replaceAll(",", "");
            double weightKg = Double.parseDouble(weightKgString);
            double weightLbs = weightKg * 2.20462;
            ((ObjectNode) jsonNode).put("mass", weightLbs + " lbs");

            ((ObjectNode) jsonNode).put("name", System.getenv("name"));

            return jsonNode;
        }
    }
}
