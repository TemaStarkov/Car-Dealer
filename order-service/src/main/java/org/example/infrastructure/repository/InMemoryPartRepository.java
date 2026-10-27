package org.example.infrastructure.repository;

import org.example.domain.model.part.Part;
import org.example.domain.model.enums.PartCategory;
import org.example.domain.model.enums.PartStatus;
import org.example.infrastructure.repository.InMemoryBaseRepository;

import java.util.List;
import java.util.Optional;

public class InMemoryPartRepository extends InMemoryBaseRepository<Part, String> {

    @Override
    protected String getId(Part part) {

        return part.getId();
    }

    @Override
    protected void validate(Part part) {

        if (part == null) {
            throw new IllegalArgumentException("Part cannot be null");
        }
    }

    public Optional<Part> findByPartNumber(String partNumber) {

        return findFirst(p -> p.getPartNumber().equals(partNumber));
    }

    public List<Part> findByCategory(PartCategory category) {

        return find(p -> p.getCategory() == category);
    }

    public List<Part> findByStatus(PartStatus status) {

        return find(p -> p.getStatus() == status);
    }

    public List<Part> findInStock() {

        return find(Part::isInStock);
    }

    public List<Part> findByManufacturer(String manufacturer) {

        return find(p -> p.getManufacturer().equalsIgnoreCase(manufacturer));
    }

    public List<Part> findByLocation(String location) {

        return find(p -> location.equals(p.getLocation()));
    }
}