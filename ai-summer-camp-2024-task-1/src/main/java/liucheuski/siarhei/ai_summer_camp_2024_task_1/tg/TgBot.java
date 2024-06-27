package liucheuski.siarhei.ai_summer_camp_2024_task_1.tg;

import liucheuski.siarhei.ai_summer_camp_2024_task_1.service.MessageHandler;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
@Component
public class TgBot extends TelegramLongPollingBot {

    private static final String BOT_NAME = "ai-summer-task-1-bot";
    private final MessageHandler messageHandler;

    public TgBot(@NotNull Environment env, MessageHandler messageHandler) throws TelegramApiException {
        super(env.getProperty("tgBotToken"));
        this.messageHandler = messageHandler;
        new TelegramBotsApi(DefaultBotSession.class).registerBot(this);
    }


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        messageHandler.handle(update, this);
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }
}
