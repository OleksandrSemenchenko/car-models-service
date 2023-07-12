package ua.com.foxminded.vehicles.entity;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "manufacturers")
@Data
public class Manufacturer implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    private String name;
    
    @OneToMany(mappedBy = "manufacturer")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Vehicle> vhicles;
}
