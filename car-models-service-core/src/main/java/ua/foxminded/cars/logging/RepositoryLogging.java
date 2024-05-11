package ua.foxminded.cars.logging;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RepositoryLogging {

  @Pointcut("execution public . ua.foxminded.repository.* ")
  void repositoryMethod() {}


}
