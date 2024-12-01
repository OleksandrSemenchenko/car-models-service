package ua.nicegear.cars.bot.commands.button;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ua.nicegear.cars.bot.config.ButtonsConfig;
import ua.nicegear.cars.bot.controller.strategy.ConsumeStrategy;
import ua.nicegear.cars.bot.controller.strategy.Context;
import ua.nicegear.cars.bot.controller.strategy.impl.BodyStyleStrategy;
import ua.nicegear.cars.bot.dto.FilterDto;
import ua.nicegear.cars.bot.enums.BodyStyle;
import ua.nicegear.cars.bot.service.FilterService;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HatchbackCommand implements ButtonCommand {

  private final TelegramClient telegramClient;
  private final ButtonsConfig buttonsConfig;
  private final FilterService filterService;

  @Override
  public void setStrategyTo(Context context) {


  }

  private void getContextWithBodyStyleStrategy(long chatId, Context context, Set<BodyStyle> bodyStyles) {
    FilterDto filterDto = FilterDto.builder().bodyStyles(bodyStyles).build();
    filterDto = filterService.updateCache(filterDto);
//    long chartId = update.getCallbackQuery().getMessage().getChatId();
    SendMessage sendMessage = buildSendMessage(chatId, filterDto);
    ConsumeStrategy bodyStyleStrategy =
      new BodyStyleStrategy(telegramClient, buttonsConfig, filterService, sendMessage);
    context.setConsumeStrategy(bodyStyleStrategy);
  }

  private SendMessage buildSendMessage(long chatId, FilterDto filterDto) {
    String message = filterDto.getBodyStyles().stream()
      .map(Enum::toString)
      .collect(Collectors.joining(", "));
    return SendMessage.builder().chatId(chatId).text(message).build();
  }
}
