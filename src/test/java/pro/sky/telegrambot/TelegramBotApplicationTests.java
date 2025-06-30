package pro.sky.telegrambot;

import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
public class TelegramBotApplicationTests {

	@TestConfiguration
	static class DisableLiquibaseConfig {
		@Bean
		public SpringLiquibase liquibase() {
			SpringLiquibase liquibase = new SpringLiquibase();
			liquibase.setShouldRun(false); // Отключаем Liquibase
			return liquibase;
		}
	}

	@Test
	void contextLoads() {
	}
}