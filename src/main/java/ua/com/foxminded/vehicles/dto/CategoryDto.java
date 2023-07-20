package ua.com.foxminded.vehicles.dto;

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
public class CategoryDto {
    
    private String name;
    
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<VehicleDto> vehicles;
}
