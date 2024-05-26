package ua.foxminded.cars.service.impls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ua.foxminded.cars.TestDataGenerator;
import ua.foxminded.cars.exceptionhandler.exceptions.ManufacturerNotFoundException;
import ua.foxminded.cars.mapper.ManufacturerMapper;
import ua.foxminded.cars.repository.ManufacturerRepository;
import ua.foxminded.cars.repository.entity.Manufacturer;
import ua.foxminded.cars.service.dto.ManufacturerDto;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceImplTest {

  private final String NOT_EXISTING_MANUFACTURER = "Nissan";
  private static final String MANUFACTURER_NAME = "BMW";

  @InjectMocks private ManufacturerServiceImpl manufacturerService;

  @Mock private ManufacturerRepository manufacturerRepository;

  @BeforeEach
  void setUp() {
    ManufacturerMapper manufacturerMapper = Mappers.getMapper(ManufacturerMapper.class);
    ReflectionTestUtils.setField(manufacturerService, "manufacturerMapper", manufacturerMapper);
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
