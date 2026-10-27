package org.example.infrastructure.repository;

import org.example.domain.model.user.*;
import org.example.domain.model.enums.UserRole;
import org.example.infrastructure.repository.InMemoryBaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class InMemoryUserRepository extends InMemoryBaseRepository<User, String> {

    private final Random random = new Random();

    @Override
    protected String getId(User user) {

        return user.getId();
    }

    @Override
    protected void validate(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
    }

    public List<User> findByRole(UserRole role) {

        return find(u -> u.getRole() == role);
    }

    public List<Customer> findAllCustomers() {

        return storage.values().stream()
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .toList();
    }

    public List<Manager> findAllManagers() {

        return storage.values().stream()
                .filter(u -> u instanceof Manager)
                .map(u -> (Manager) u)
                .toList();
    }

    public Optional<Manager> findRandomManager() {

        List<Manager> managers = findAllManagers();
        if (managers.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(managers.get(random.nextInt(managers.size())));
    }
}