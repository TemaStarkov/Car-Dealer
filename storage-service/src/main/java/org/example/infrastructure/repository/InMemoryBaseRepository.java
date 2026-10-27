package org.example.infrastructure.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class InMemoryBaseRepository<T, ID> implements BaseRepository<T, ID> {

    protected final Map<ID, T> storage = new ConcurrentHashMap<>();

    protected abstract ID getId(T entity);
    protected abstract void validate(T entity);

    @Override
    public T save(T entity) {
        validate(entity);
        ID id = getId(entity);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {

        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<T> findAll() {

        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(ID id) {

        storage.remove(id);
    }

    @Override
    public boolean existsById(ID id) {

        return storage.containsKey(id);
    }

    @Override
    public long count() {

        return storage.size();
    }

    protected List<T> find(Predicate<T> predicate) {

        return storage.values().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    protected Optional<T> findFirst(Predicate<T> predicate) {

        return storage.values().stream()
                .filter(predicate)
                .findFirst();
    }

    public void clear() {

        storage.clear();
    }
}