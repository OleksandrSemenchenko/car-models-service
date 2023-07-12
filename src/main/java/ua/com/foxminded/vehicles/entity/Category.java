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
@Table(name = "categories")
@Data
public class Category implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    private String name;
    
    @OneToMany(mappedBy = "category")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Vehicle> vehicles;
}
