package ua.com.foxminded.vehicles.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "vehicles")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String id;
    
    @Column(name = "production_year")
    @ToString.Include
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
