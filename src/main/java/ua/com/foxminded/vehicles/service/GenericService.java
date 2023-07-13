package ua.com.foxminded.vehicles.service;

import java.util.List;

import ua.com.foxminded.vehicles.exception.ServiceException;

public interface GenericService<T> {
    
    public T create(T model) throws ServiceException;

    public List<T> getAll() throws ServiceException;
    
    public T update(T model) throws ServiceException;
}
