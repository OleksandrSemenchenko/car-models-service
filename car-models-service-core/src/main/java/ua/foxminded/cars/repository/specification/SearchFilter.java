package ua.foxminded.cars.repository.specification;

import jakarta.validation.constraints.Positive;
import java.time.Year;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFilter {

  private String name;
  private String category;
  private String manufacturer;

  @Positive private Year maxYear;

  @Positive private Year minYear;

  @Positive private Year year;
}
