package ua.nicegear.cars.bot.dto;

import java.util.List;
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
  private String name;
  private Integer year;
  private String manufacturer;
  private List<String> categories;
}
