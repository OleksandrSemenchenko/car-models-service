package ua.com.foxminded.vehicles.dto;

import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class VehicleDto {

    @NotNull
    @EqualsAndHashCode.Include
    @ToString.Include
    private String id;
    
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer productionYear;
    
    private ManufacturerDto manufacturer;
    
    private ModelDto model;
    
    private Set<CategoryDto> categories;
    
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
