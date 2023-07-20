package ua.com.foxminded.vehicles.model;

import java.io.Serializable;
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
public class ModelDto implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @NotBlank
    private String name;
    
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<VehicleDto> vehicles;
}
