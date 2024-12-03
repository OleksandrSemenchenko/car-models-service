package ua.nicegear.cars.bot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.nicegear.cars.bot.repository.entity.Filter;

public interface FilterRepository extends MongoRepository<Filter, Long> {

  Filter findByChatId(Long id);
}
