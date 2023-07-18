package ua.com.foxminded.vehicles.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GenericService<T> {
    
    public T save(T model);

    public Page<T> getAll(Pageable page);
}
