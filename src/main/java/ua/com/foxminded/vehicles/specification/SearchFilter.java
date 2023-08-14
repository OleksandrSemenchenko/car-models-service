package ua.com.foxminded.vehicles.specification;

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
    private Integer maxYear;
    private Integer minYear;
}
