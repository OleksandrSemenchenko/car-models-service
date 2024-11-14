package ua.nicegear.cars.bot.buttons;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public abstract class Button {

  protected abstract SendMessage addButtonTo(SendMessage sendMessage);
}
