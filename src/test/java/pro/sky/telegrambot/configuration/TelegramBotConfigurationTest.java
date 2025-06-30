package pro.sky.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class TelegramBotConfigurationTest {

    @Autowired
    private TelegramBot telegramBot;

    @Test
    void telegramBotBean_ShouldBeCreated() {
        assertNotNull(telegramBot);
    }

    @Configuration
    static class TestConfig {
        @Bean
        public TelegramBot telegramBot() {
            return new TelegramBot("test-token");
        }
    }
}