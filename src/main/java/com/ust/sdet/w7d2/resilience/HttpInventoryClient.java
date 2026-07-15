package com.ust.sdet.w7d2.resilience;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class HttpInventoryClient implements InventoryClient {
    private final String baseUrl;
    private final Duration requestTimeout;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpInventoryClient(String baseUrl, Duration requestTimeout) {
        this.baseUrl = baseUrl.replaceFirst("/+$", "");
        this.requestTimeout = requestTimeout;
        this.httpClient = HttpClient.newBuilder().connectTimeout(requestTimeout).build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public InventoryAvailability availabilityFor(String sku) {
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(baseUrl + "/inventory/" + URLEncoder.encode(sku, StandardCharsets.UTF_8))
            )
            .timeout(requestTimeout)
            .GET()
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new InventoryDependencyException("Inventory returned HTTP " + response.statusCode());
            }
            return parseAvailability(response.body());
        } catch (IOException error) {
            throw new InventoryDependencyException("Inventory call failed", error);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new InventoryDependencyException("Inventory call was interrupted", error);
        }
    }

    private InventoryAvailability parseAvailability(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode inStock = root.get("inStock");
            if (inStock == null || !inStock.isBoolean()) {
                throw new InventoryDependencyException("Inventory response requires boolean inStock");
            }
            return new InventoryAvailability(inStock.booleanValue());
        } catch (JsonProcessingException error) {
            throw new InventoryDependencyException("Inventory returned malformed JSON", error);
        }
    }
}
