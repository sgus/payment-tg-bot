package my.app.handler;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface TelegramMessageHandler {
    void handle(Message message);
}
