package ua.foxminded.cars.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDto {

  private UUID id;

  @NotBlank private String name;

  @NotNull private Integer year;

  @NotBlank private String manufacturer;

  @NotNull private Set<@NotBlank String> categories;
}
