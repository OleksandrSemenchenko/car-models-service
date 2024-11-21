package ua.nicegear.cars.bot.controller.strategies;

import io.micrometer.observation.Observation;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class ResponseProcessor {

  protected  <T, R, E extends TelegramApiException> R processResponse(
    Observation.CheckedFunction<T, R, E> consumer, T response) {
    try {
      return consumer.apply(response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
