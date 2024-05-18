package ua.foxminded.cars.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.foxminded.cars.repository.specification.SearchFilter;
import ua.foxminded.cars.service.dto.ModelDto;

public interface ModelService {

  boolean isModelExistById(UUID modelId);

  ModelDto updateModel(ModelDto model);

  void deleteModelById(UUID id);

  ModelDto getModelById(UUID modelId);

  ModelDto getModel(String manufacturer, String modelName, int year);

  Page<ModelDto> searchModel(SearchFilter searchFilter, Pageable pageable);

  ModelDto createModel(ModelDto model);
}
