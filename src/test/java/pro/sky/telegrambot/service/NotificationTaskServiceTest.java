package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.exception.ReminderParseException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationTaskServiceTest {

    @Mock
    private NotificationTaskRepository repository;

    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private NotificationTaskService service;

    @Test
    void processMessage_ValidInput_CreatesTask() {
        // Подготовка
        String validMessage = "31.12.2023 23:59 Поздравить с НГ";
        when(telegramBot.execute(any(SendMessage.class)))
                .thenReturn(mock(SendResponse.class));

        // Выполнение
        service.processMessage(123L, validMessage);

        // Проверка
        verify(repository, times(1)).save(any(NotificationTask.class));
    }

    @Test
    void processMessage_InvalidFormat_ThrowsException() {
        // Подготовка неверных форматов
        String[] invalidMessages = {
                "неправильный формат",
                "01.01.2023",                  // Только дата
                "12:00 Текст без даты",        // Только время
                "01-01-2023 12:00 Не тот разделитель",
                "01.01.23 12:00 Двухзначный год"
        };

        // Проверка всех вариантов
        for (String invalidMessage : invalidMessages) {
            assertThrows(ReminderParseException.class,
                    () -> service.processMessage(123L, invalidMessage),
                    "Должно выбрасываться исключение для: " + invalidMessage
            );
        }
    }

    @Test
    void checkReminders_FindsDueTasks_SendsMessages() {
        // Подготовка
        NotificationTask task = new NotificationTask();
        task.setChatId(123L);
        task.setMessage("Тест");
        task.setReminderTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        when(repository.findByReminderTimeAndIsSentFalse(any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(task));
        when(telegramBot.execute(any(SendMessage.class)))
                .thenReturn(mock(SendResponse.class));

        // Выполнение
        service.checkReminders();

        // Проверка
        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(messageCaptor.capture());

        SendMessage actualMessage = messageCaptor.getValue();
        assertEquals(task.getChatId(), actualMessage.getParameters().get("chat_id"));
        assertEquals("🔔 Напоминание: " + task.getMessage(), actualMessage.getParameters().get("text"));

        assertTrue(task.isSent());
        verify(repository).save(task);
    }

}
