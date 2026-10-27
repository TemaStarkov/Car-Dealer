package org.example.integration.rest.dto.configuration;

import java.util.Map;

public record ConfigureCarRequest(
        String modelId,
        Map<String, String> componentChoices
) {
}
