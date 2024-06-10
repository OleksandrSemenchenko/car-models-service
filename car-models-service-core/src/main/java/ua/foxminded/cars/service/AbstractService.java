package ua.foxminded.cars.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public abstract class AbstractService {

  protected Pageable setDefaultSortIfNecessary(
      Pageable pageable, Direction sortDirection, String sortBy) {
    if (pageable.getSort().isUnsorted()) {
      Sort defaulSort = Sort.by(sortDirection, sortBy);
      return PageRequest.of(
          pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(defaulSort));
    }
    return pageable;
  }
}
