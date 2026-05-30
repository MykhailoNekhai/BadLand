package ua.uni.base_repo;

import ua.uni.objects.ID;

import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    Optional<T> findById(ID id);
    List<T> getAll();
    void add(T object);
    void delete(T object);
    void update(T object);
    ID generateID();
}
