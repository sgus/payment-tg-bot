package my.app.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import my.app.component.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AutoReplyTelegramMessageHandler implements TelegramMessageHandler {
    Bot bot;

    @Override
    public void handle(Message message) {
        if (message.getText().startsWith("/help")) {
            return;
        }
        Long chatId = message.getChatId();
        String text = message.getText();
        bot.sendMsg(chatId, "you said " + text);
        return;
    }
}
