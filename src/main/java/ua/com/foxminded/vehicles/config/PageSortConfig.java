package ua.com.foxminded.vehicles.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Sort.Direction;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties
@Getter
@Setter
public class PageSortConfig {
    
    private String categorySortParameter;
    private Direction categorySortDirection;
    private String manufacturerSortParameter;
    private Direction manufacturerSortDirection;
    private String modelSortParameter;
    private Direction modelSortDirection;
    private String vehicleSortParameter;
    private Direction vehicleSortDirection;
}
