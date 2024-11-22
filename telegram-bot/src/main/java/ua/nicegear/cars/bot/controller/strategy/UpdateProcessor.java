package ua.nicegear.cars.bot.controller.strategy;

import io.micrometer.observation.Observation;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class UpdateProcessor {

  protected <T, R, E extends TelegramApiException> R processUpdate(
      Observation.CheckedFunction<T, R, E> consumer, T response) {
    try {
      return consumer.apply(response);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
