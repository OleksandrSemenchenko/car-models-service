package ua.foxminded.cars.service.impls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import ua.foxminded.cars.TestDataGenerator;
import ua.foxminded.cars.config.PageSortConfig;
import ua.foxminded.cars.exceptionhandler.exceptions.ManufacturerNotFoundException;
import ua.foxminded.cars.mapper.ManufacturerMapper;
import ua.foxminded.cars.repository.ManufacturerRepository;
import ua.foxminded.cars.repository.entity.Manufacturer;
import ua.foxminded.cars.service.dto.ManufacturerDto;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceImplTest {

  private static final String NOT_EXISTING_MANUFACTURER = "Nissan";
  private static final String MANUFACTURER_NAME = "BMW";
  private static final int FIVE_ELEMENTS = 5;
  private static final int FIRST_PAGE = 1;

  @InjectMocks private ManufacturerServiceImpl manufacturerService;

  @Mock private ManufacturerRepository manufacturerRepository;

  @Mock private PageSortConfig pageSortConfig;

  @BeforeEach
  void setUp() {
    ManufacturerMapper manufacturerMapper = Mappers.getMapper(ManufacturerMapper.class);
    ReflectionTestUtils.setField(manufacturerService, "manufacturerMapper", manufacturerMapper);
  }

  @Test
  void getAllManufacturers_shouldReturnSortedPage_whenRequestHasSorting() {
    Manufacturer manufacturer = TestDataGenerator.generateManufacturer();
    Sort sortByName = Sort.by("name");
    Pageable pageable = PageRequest.of(FIRST_PAGE, FIVE_ELEMENTS, sortByName);
    Page<Manufacturer> manufacturersPage = new PageImpl<>(List.of(manufacturer));

    when(manufacturerRepository.findAll(any(Pageable.class))).thenReturn(manufacturersPage);

    Page<ManufacturerDto> actualManufacturersPage =
        manufacturerService.getAllManufacturers(pageable);
    ManufacturerDto actualManufacturer = actualManufacturersPage.getContent().get(0);

    assertEquals(MANUFACTURER_NAME, actualManufacturer.getName());
  }

  @Test
  void getAllManufacturers_shouldReturnSortedPage_whenRequestHasNoSorting() {
    Manufacturer manufacturer = TestDataGenerator.generateManufacturer();
    Pageable pageable = Pageable.ofSize(5);
    Page<Manufacturer> manufacturersPage = new PageImpl<>(List.of(manufacturer));

    when(pageSortConfig.getManufacturerSortDirection()).thenReturn(Sort.Direction.DESC);
    when(pageSortConfig.getManufacturerSortBy()).thenReturn("name");
    when(manufacturerRepository.findAll(any(Pageable.class))).thenReturn(manufacturersPage);

    Page<ManufacturerDto> actualManufacturersPage =
        manufacturerService.getAllManufacturers(pageable);
    ManufacturerDto actualManufacturer = actualManufacturersPage.getContent().get(0);

    assertEquals(MANUFACTURER_NAME, actualManufacturer.getName());
  }

  @Test
  void getManufacturer_shouldReturnManufacturer_whenManufacturerIsInDb() {
    Manufacturer manufacturer = TestDataGenerator.generateManufacturer();

    when(manufacturerRepository.findById(MANUFACTURER_NAME)).thenReturn(Optional.of(manufacturer));

    ManufacturerDto manufacturerDto = manufacturerService.getManufacturer(MANUFACTURER_NAME);

    assertEquals(MANUFACTURER_NAME, manufacturerDto.getName());
  }

  @Test
  void getManufacturer_shouldThrowException_whenNoManufacturerInDb() {
    when(manufacturerRepository.findById(NOT_EXISTING_MANUFACTURER)).thenReturn(Optional.empty());

    assertThrows(
        ManufacturerNotFoundException.class,
        () -> manufacturerService.getManufacturer(NOT_EXISTING_MANUFACTURER));
  }
}
