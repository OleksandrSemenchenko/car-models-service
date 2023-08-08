package ua.com.foxminded.vehicles.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterParameter {
    
    private String modelName;
    private String categoryName;
    private String manufacturerName;
    private Integer maxYear;
    private Integer minYear;
}
