package ua.com.foxminded.vehicles.entity;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    private String name;
    
    @Column(name = "production_year")
    private Integer productionYear;
    
    @ManyToOne
    @JoinColumn(name = "manufacturer_name")
    private Manufacturer manufacturer;
    
    @ManyToOne
    @JoinColumn(name = "model_name")
    private Model model;
    
    @ManyToMany
    @JoinTable(name = "vehicle_category", 
               joinColumns = @JoinColumn(name = "vehicle_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "category_name", referencedColumnName = "name"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Category> categories;
    
    public void addCategory(Category category) {
        this.categories.add(category);
        category.getVehicles().add(this);
    }
    
    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getVehicles().remove(this);
    }
}
