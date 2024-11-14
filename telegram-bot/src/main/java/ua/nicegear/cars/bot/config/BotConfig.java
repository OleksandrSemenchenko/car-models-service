package ua.nicegear.cars.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class BotConfig {

  @Bean
  TelegramClient telegramClient(@Value("${bot.token}") String token) {
    return new OkHttpTelegramClient(token);
  }
}
