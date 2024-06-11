package ua.foxminded.cars.repository;

import java.time.Year;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.foxminded.cars.repository.entity.Model;

public interface ModelRepository
    extends JpaRepository<Model, UUID>, JpaSpecificationExecutor<Model> {

  @Modifying
  @Query(
      value =
          """
    delete from model_category
    	where model_id = :modelId AND category_name = :categoryName
    """,
      nativeQuery = true)
  void removeModelFromCategory(
      @Param("modelId") String modelId, @Param("categoryName") String categoryName);

  @Modifying
  @Query(
      value =
          """
    insert into model_category(model_id, category_name)
    	values(:modelId, :categoryName);
    """,
      nativeQuery = true)
  void putModelToCategory(
      @Param("modelId") UUID modelId, @Param("categoryName") String categoryName);

  boolean existsByYearValue(Year year);

  boolean existsByCategoriesName(String categoryName);

  boolean existsByManufacturerName(String manufacturerName);
}
