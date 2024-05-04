package ua.com.foxminded.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.com.foxminded.repository.specification.SearchFilter;
import ua.com.foxminded.service.dto.ModelDto;

public interface ModelService {

  ModelDto updateModel(ModelDto modelDto);

  void deleteModelById(String id);

  ModelDto getModelById(String modelId);

  ModelDto getModel(String manufacturer, String modelName, int year);

  Page<ModelDto> search(SearchFilter searchFilter, Pageable pageable);

  ModelDto create(ModelDto model);
}
