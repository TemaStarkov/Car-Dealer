package org.example.application.service;

import org.example.integration.rest.dto.assembly.AssemblyOrderDto;
import org.example.integration.rest.dto.assembly.CreateAssemblyOrderRequest;
import org.example.integration.rest.dto.assembly.UpdateAssemblyOrderRequest;

import java.util.List;
import java.util.UUID;

public interface AssemblyOrderService {

    AssemblyOrderDto create(CreateAssemblyOrderRequest request);

    List<AssemblyOrderDto> findAll();

    AssemblyOrderDto findById(UUID id);

    AssemblyOrderDto update(UUID id, UpdateAssemblyOrderRequest request);

    void delete(UUID id);
}