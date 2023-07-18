package ua.com.foxminded.vehicles.entity;

import java.io.Serializable;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "vehicles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "production_year")
    private Integer productionYear;
    
    @ManyToOne
    @JoinColumn(name = "manufacturer_name")
    private ManufacturerEntity manufacturer;
    
    @ManyToOne
    @JoinColumn(name = "model_name")
    private ModelEntity model;
    
    @ManyToMany
    @JoinTable(name = "vehicle_category", 
               joinColumns = @JoinColumn(name = "vehicle_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "category_name", referencedColumnName = "name"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CategoryEntity> categories;
    
    public void addCategory(CategoryEntity category) {
        this.categories.add(category);
        category.getVehicles().add(this);
    }
    
    public void removeCategory(CategoryEntity category) {
        this.categories.remove(category);
        category.getVehicles().remove(this);
    }
}
