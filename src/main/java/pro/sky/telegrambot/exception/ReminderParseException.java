package pro.sky.telegrambot.exception;

public class ReminderParseException extends RuntimeException {
    private final String invalidInput;

    public ReminderParseException(String message, String invalidInput) {
        super(message);
        this.invalidInput = invalidInput;
    }

    public String getInvalidInput() {
        return invalidInput;
    }
}