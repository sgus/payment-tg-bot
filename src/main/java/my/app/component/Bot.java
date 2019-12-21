package my.app.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import my.app.handler.TelegramMessageHandler;
import my.app.service.TelegramUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource("classpath:bot.properties")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.name}")
    private String botUserName;

    final TelegramUpdateService telegramUpdateService;
    final List<TelegramMessageHandler> telegramMessageHandler;

    @Autowired
    public Bot(TelegramUpdateService telegramUpdateService, @Lazy List<TelegramMessageHandler> telegramMessageHandler) {
        this.telegramUpdateService = telegramUpdateService;
        this.telegramMessageHandler = telegramMessageHandler;
    }

    @Override
    public String getBotUsername() {
        return this.botUserName;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasCallbackQuery()) {
            try {
                execute(new SendMessage().setText(update.getCallbackQuery().getData()).setChatId(update.getMessage().getChatId()) );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        telegramMessageHandler.forEach(telegramMessageHandler -> telegramMessageHandler.handle(update.getMessage()));

    }



    public synchronized void sendMsg(Long chatId, String msg){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true); // включить локальную работу с клиентами
        sendMessage.setChatId(chatId);// выбираем чат отправки сообщений
        //sendMessage.setReplyToMessageId(message.getMessageId());//на какое сообщение будем отвечать
        sendMessage.setText(msg);
        try {
          //  getReplyKeyboardMarkup(sendMessage);
            getInlineKeyboardMarkup(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private ReplyKeyboardMarkup getReplyKeyboardMarkup(SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();//инит клавы
        sendMessage.setReplyMarkup(replyKeyboardMarkup); //связываем разметку с клавой
        replyKeyboardMarkup.setSelective(true); // отобразить для всеъ пользователей
        replyKeyboardMarkup.setResizeKeyboard(true);//автоматически подгонять клаву
        replyKeyboardMarkup.setOneTimeKeyboard(true);// скрыть клавиатуру после нажатия

        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("button1"));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("button2"));

        keyboardRowList.add(keyboardFirstRow);
        keyboardRowList.add(keyboardSecondRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(SendMessage sendMessage){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("кнопка1"); // надпись на кнопке
        inlineKeyboardButton1.setCallbackData("Button \"кнопка1\" has been pressed"); //Что будет отсылатся серверу при нажатии на кнопку

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>(); // отвечает за строку кнопок
        keyboardButtonsRow1.add(inlineKeyboardButton1);

        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("кнопка2"); // надпись на кнопке
        inlineKeyboardButton2.setCallbackData("Button \"кнопка2\" has been pressed"); //Что будет отсылатся серверу при нажатии на кнопку

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(inlineKeyboardButton2);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow3.add(new InlineKeyboardButton().setText("кнопка3").setCallbackData("Button \"кнопка3\" has been pressed"));
        keyboardButtonsRow3.add(new InlineKeyboardButton().setText("кнопка4").setCallbackData("Button \"кнопка4\" has been pressed"));

        List<List<InlineKeyboardButton>> rowList= new ArrayList<>(); // обьеденить ряды
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);

        keyboardMarkup.setKeyboard(rowList); //установить кнопки в обьект разметки клавиатуры.

        sendMessage.setText("buttons").setReplyMarkup(keyboardMarkup);
        return keyboardMarkup;
    }

}
