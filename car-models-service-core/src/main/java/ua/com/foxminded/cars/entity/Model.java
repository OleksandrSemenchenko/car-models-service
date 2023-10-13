package ua.com.foxminded.cars.entity;

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
@Table(name = "models")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Model {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String id;
    
    @ToString.Include
    @JoinColumn(name = "model_year")
    @Column(name = "model_year")
    private Integer year;
    
    @ManyToOne
    @JoinColumn(name = "manufacturer_name")
    private Manufacturer manufacturer;
    
    @ManyToOne
    @JoinColumn(name = "name")
    private ModelName modelName;
    
    @ManyToMany
    @JoinTable(name = "model_category", 
               joinColumns = @JoinColumn(name = "model_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "category_name", referencedColumnName = "name"))
    private Set<Category> categories;
    
    public void addCategory(Category category) {
        this.categories.add(category);
        category.getModels().add(this);
    }
    
    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getModels().remove(this);
    }
}
