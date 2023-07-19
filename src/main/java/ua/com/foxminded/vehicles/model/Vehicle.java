package ua.com.foxminded.vehicles.model;

import java.io.Serializable;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @NotNull
    private String id;
    private Integer productionYear;
    private Manufacturer manufacturer;
    private Model model;
    
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Category> categories;
    
    public boolean hasManufacturer() {
        return manufacturer != null;
    }
    
    public boolean hasModel() {
        return model != null;
    }
    
    public boolean hasCategories() {
        return categories != null;
    }
}
