package ua.com.foxminded.vehicles.model;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class Model implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String name;
    
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Vehicle> vehicles;
}
