package ua.foxminded.cars.service.impls;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public abstract class AbstractService {

  protected Pageable setDefaultSortIfNecessary(
      Pageable pageRequest, Direction sortDirection, String sortBy) {
    if (pageRequest.getSort().isUnsorted()) {
      Sort defaulSort = Sort.by(sortDirection, sortBy);
      return PageRequest.of(
          pageRequest.getPageNumber(),
          pageRequest.getPageSize(),
          pageRequest.getSortOr(defaulSort));
    }
    return pageRequest;
  }
}
