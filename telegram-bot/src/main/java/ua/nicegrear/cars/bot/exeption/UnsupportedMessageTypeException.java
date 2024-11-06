package ua.nicegrear.cars.bot.exeption;

import lombok.Getter;

@Getter
public class UnsupportedMessageTypeException extends RuntimeException {

  private String chatId;

  public UnsupportedMessageTypeException(String chatId) {
    super(ExceptionMessage.UNSUPPORTED_MESSAGE_TYPE);
    this.chatId = chatId;
  }
}
