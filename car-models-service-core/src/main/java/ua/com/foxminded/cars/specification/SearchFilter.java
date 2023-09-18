package ua.com.foxminded.cars.specification;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFilter {
    
    private String model;
    private String category;
    private String manufacturer;
    
    @Positive
    private Integer maxYear;
    
    @Positive
    private Integer minYear;
    
    @Positive
    private Integer year;
}
