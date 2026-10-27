package org.example.integration.rest.controller;

import org.example.application.service.ConfiguratorService;
import org.example.domain.model.enums.ComponentType;
import org.example.integration.rest.dto.configuration.CarConfigurationDto;
import org.example.integration.rest.dto.configuration.ComponentDto;
import org.example.integration.rest.dto.configuration.ConfigureCarRequest;
import org.example.integration.rest.mapper.ApiMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/configurator")
@PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
public class ConfiguratorController {

    private final ConfiguratorService configuratorService;
    private final ApiMapper apiMapper;

    public ConfiguratorController(ConfiguratorService configuratorService, ApiMapper apiMapper) {

        this.configuratorService = configuratorService;
        this.apiMapper = apiMapper;
    }

    @PostMapping("/configurations")
    public CarConfigurationDto configure(@RequestBody ConfigureCarRequest request) {

        Map<ComponentType, String> componentChoices = request.componentChoices().entrySet().stream()
                .collect(Collectors.toMap(entry -> ComponentType.valueOf(entry.getKey()), Map.Entry::getValue));

        return apiMapper.toDto(configuratorService.configureCar(request.modelId(), componentChoices));
    }

    @GetMapping("/models/{modelId}/components")
    public List<ComponentDto> getCompatibleComponents(@PathVariable String modelId,
                                                      @RequestParam ComponentType componentType) {

        return configuratorService.getCompatibleComponents(modelId, componentType).stream()
                .map(apiMapper::toDto)
                .toList();
    }
}
