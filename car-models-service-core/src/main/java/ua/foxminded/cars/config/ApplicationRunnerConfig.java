/*
 * Copyright 2024 Oleksandr Semenchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ua.foxminded.cars.config;

import static java.util.Objects.isNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ua.foxminded.cars.exceptionhandler.exceptions.ResourceNotFoundException;

@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class ApplicationRunnerConfig {

  private static final String TEST_DATA_SCRIPT_PATH = "test_data.sql";

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @Bean
  ApplicationRunner applicationRunner() {
    return args -> {
      try (Connection connection = DriverManager.getConnection(url, username, password);
          Statement statement = connection.createStatement();
          InputStream inputStream =
              getClass().getClassLoader().getResourceAsStream(TEST_DATA_SCRIPT_PATH)) {
        verifyInputStream(inputStream);
        String script = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        statement.execute(script);
      }
    };
  }

  private void verifyInputStream(InputStream inputStream) {
    if (isNull(inputStream)) {
      throw new ResourceNotFoundException();
    }
  }
}
