package ua.foxminded.cars.validation;

import static java.util.Objects.nonNull;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ua.foxminded.cars.exceptionhandler.exceptions.PeriodNotValidException;
import ua.foxminded.cars.repository.specification.SearchFilter;

@Constraint(validatedBy = PeriodIncluded.PeriodValidatorForSearchFilter.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PeriodIncluded {

  String message() default "The maxYear=%s must be after minYear=%s";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class PeriodValidatorForSearchFilter
      implements ConstraintValidator<PeriodIncluded, SearchFilter> {

    @Override
    public boolean isValid(SearchFilter searchFilter, ConstraintValidatorContext context) {

      Integer minYear = searchFilter.getMinYear();
      Integer maxYear = searchFilter.getMaxYear();

      if (nonNull(minYear) && nonNull(maxYear) && minYear >= maxYear) {
        throw new PeriodNotValidException(minYear, maxYear);
      }
      return true;
    }
  }
}
