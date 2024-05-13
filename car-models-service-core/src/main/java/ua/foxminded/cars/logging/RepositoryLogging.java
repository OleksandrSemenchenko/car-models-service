package ua.foxminded.cars.logging;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RepositoryLogging {

  @Pointcut("execution(public * ua.foxminded.cars.repository.*Repository.*(..))")
  void repositoryMethod() {}

  @Around("repositoryMethod() ")
  public Object addLoggingToRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
    String className = joinPoint.getSignature().getDeclaringTypeName();
    String methodName = joinPoint.getSignature().getName();
    String arguments = Arrays.toString(joinPoint.getArgs());
    log.warn(String.format("Request: %s.%s.s.(%s)", className, methodName, arguments));
    Object result = joinPoint.proceed();
    log.warn(
        String.format(
            "Response: %s.%s.(%s) Returned: %s", className, methodName, arguments, result));
    return result;
  }
}
