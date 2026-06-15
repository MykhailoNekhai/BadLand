package ua.uni.platform.repository;

import ua.uni.core.value.ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class PRepository<T> implements IRepository<T> {

    protected Map<ID, T> db = new HashMap<>();

    protected abstract ID getId(T object);


    @Override
    public Optional <T> findById(ID id) {
        return Optional.ofNullable(db.get(id));
    }


    @Override
    public List <T> getAll() {
        return List.copyOf(db.values());
    }

    @Override
    public void add(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Об'єкт не може бути null");
        }
        // IF ALREADY EXIST CHECK
        ID id = getId(object);
        if (db.containsKey(id)) {
            throw new IllegalArgumentException("Object with this id already exist");
        }
        // IF ALL GOOD, PUT OBJECT
        db.put(id, object);
    }

    public void replaceAll(List<T> items) {
        db.clear();
        for (T item : items) {
            db.put(getId(item), item);
        }
    }

    @Override
    public ID generateID() {
        if (db.isEmpty()) {
            return new ID(1);
        }

        int max = 0;
        for (ID tryId : db.keySet()) {
            int number = tryId.getID();
            if (number > max) {
                max = number;
            }
        }
        return new ID(max + 1);
    }

    @Override
    public void delete(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Object can not be null");
        }
        ID id = getId(object);
        db.remove(id);
    }

    @Override
    public void update(T object) {
        // NULL CHECK
        if (object == null) {
            throw new IllegalArgumentException("Object can not be null");
        }

        ID id = getId(object);

        if (!db.containsKey(id)) {
            throw new IllegalArgumentException("No object with those parameters");
        }
        // IF ALL IS FINE, PUT OBJECT
        db.put(id, object);
    }
}
