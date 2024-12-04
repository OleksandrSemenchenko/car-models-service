package ua.nicegear.cars.bot.repository.entity;

import java.util.Set;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ua.nicegear.cars.bot.enums.BodyStyle;

@Document("filters")
@Data
public class Filter {

  @Id Long chatId;
  Integer maxYear;
  Integer minYear;
  Integer maxMileage;
  Integer numberOfOwners;
  Set<BodyStyle> bodyStyles;
}
