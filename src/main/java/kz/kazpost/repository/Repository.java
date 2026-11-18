package kz.kazpost.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository contract shared by persistence implementations.
 */
public abstract class Repository<T> {
    public abstract void save(T entity);
    public abstract Optional<T> findById(String id);
    public abstract List<T> findAll();
}