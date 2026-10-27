package org.example.integration.rest.controller;

import org.example.application.service.AssemblyOrderService;
import org.example.integration.rest.dto.assembly.AssemblyOrderDto;
import org.example.integration.rest.dto.assembly.CreateAssemblyOrderRequest;
import org.example.integration.rest.dto.assembly.UpdateAssemblyOrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/storage/assembly-orders")
public class AssemblyOrderController {

    private final AssemblyOrderService assemblyOrderService;

    public AssemblyOrderController(AssemblyOrderService assemblyOrderService) {
        this.assemblyOrderService = assemblyOrderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_ADMIN')")
    public AssemblyOrderDto create(@RequestBody CreateAssemblyOrderRequest request) {
        return assemblyOrderService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_ADMIN')")
    public List<AssemblyOrderDto> findAll() {
        return assemblyOrderService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_ADMIN')")
    public AssemblyOrderDto findById(@PathVariable UUID id) {
        return assemblyOrderService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_ADMIN')")
    public AssemblyOrderDto update(@PathVariable UUID id,
                                   @RequestBody UpdateAssemblyOrderRequest request) {
        return assemblyOrderService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_ADMIN')")
    public void delete(@PathVariable UUID id) {
        assemblyOrderService.delete(id);
    }
}