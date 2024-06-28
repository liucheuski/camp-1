package liucheuski.siarhei.ai_summer_camp_2024_task_1.service.impl;

import liucheuski.siarhei.ai_summer_camp_2024_task_1.service.TgService;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.tg.TgBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.File;

@Service
@RequiredArgsConstructor
class TgServiceImpl implements TgService {
    private final TgBot bot;

    @Override
    public File retriveFile(String fileId) throws TelegramApiException {
        org.telegram.telegrambots.meta.api.objects.File tgFile = bot.execute(GetFile.builder()
                .fileId(fileId)
                .build());
        return bot.downloadFile(tgFile);
    }

    @Override
    public void sendAudio(byte[] bytes, long chatId) throws TelegramApiException {
        bot.execute(SendAudio.builder()
                .chatId(chatId)
                .audio(new InputFile(new ByteArrayInputStream(bytes), System.currentTimeMillis() + ".aac"))
                .build());
    }
}
