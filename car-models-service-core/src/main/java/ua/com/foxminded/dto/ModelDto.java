package ua.com.foxminded.dto;

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
public class ModelDto {

    private String id;
    private String name;
    private Integer year;
    private String manufacturer;
    
    @NotNull
    private Set<@NotNull @NotEmpty String> categories;
}
