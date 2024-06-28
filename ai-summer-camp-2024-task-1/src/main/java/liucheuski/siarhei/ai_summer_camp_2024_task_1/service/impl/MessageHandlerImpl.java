package liucheuski.siarhei.ai_summer_camp_2024_task_1.service.impl;

import com.theokanning.openai.threads.Thread;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.service.MessageHandler;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.service.OpenAiFacade;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.service.TgService;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class MessageHandlerImpl implements MessageHandler {

    public static final String DEFAULT_ANSWER = "Sorry, but I understand the only voice messages. Try it out, please";

    private final TgService tgService;
    private final OpenAiFacade openAiFacade;

    public MessageHandlerImpl(@Lazy TgService tgService, OpenAiFacade openAiFacade) {
        this.tgService = tgService;
        this.openAiFacade = openAiFacade;
    }


    @Override
    public void handle(Update upd, TelegramLongPollingBot bot) throws TelegramApiException, IOException {
        Message message = Optional.ofNullable(upd.getMessage())
                .orElseThrow(() -> new TelegramApiException("No message"));
        Long chatId = message.getChatId();
        if (message.hasVoice()) {
            String fileId = message.getVoice().getFileId();
            File file = tgService.retriveFile(fileId);
            File ogaFile = FileUtil.getOgaFile(file);
            String question = openAiFacade.speechIntoText(ogaFile, upd);
            Thread thread = openAiFacade.getThread(chatId);
            String answer = openAiFacade.getAnswer(thread.getId(), question);
            byte[] bytes = openAiFacade.textIntoSpeech(answer);
            tgService.sendAudio(bytes, chatId);
        } else {
            byte[] bytes = openAiFacade.textIntoSpeech(DEFAULT_ANSWER);
            tgService.sendAudio(bytes, chatId);
        }
    }
}
