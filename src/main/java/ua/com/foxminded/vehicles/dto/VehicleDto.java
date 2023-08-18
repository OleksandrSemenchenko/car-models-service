package ua.com.foxminded.vehicles.dto;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {

    private String id;
    
    @NotNull
    @NotEmpty
    private Integer year;
    
    @NotNull
    @NotEmpty
    private String manufacturer;
    
    @NotNull
    @NotEmpty
    private String model;
    
    private Set<@NotNull @NotEmpty String> categories;
}
