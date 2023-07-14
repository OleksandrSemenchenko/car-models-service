package ua.com.foxminded.vehicles.service;

import java.util.List;

public interface GenericService<T> {
    
    public T save(T model);

    public List<T> getAll();
}
