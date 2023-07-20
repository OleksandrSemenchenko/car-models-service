package ua.com.foxminded.vehicles.model;

import java.io.Serializable;
import java.util.Set;

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
public class CategoryDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<VehicleDto> vehicles;
}
