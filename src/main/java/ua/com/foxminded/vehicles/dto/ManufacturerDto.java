package ua.com.foxminded.vehicles.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturerDto {

    @NotBlank
    private String name;
    
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<VehicleDto> vehicles;
}
