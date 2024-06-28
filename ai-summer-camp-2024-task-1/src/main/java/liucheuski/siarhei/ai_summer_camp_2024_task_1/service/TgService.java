package liucheuski.siarhei.ai_summer_camp_2024_task_1.service;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

public interface TgService {

    File retriveFile(String fileId) throws TelegramApiException;

    void sendAudio(byte[] bytes, long chatId) throws TelegramApiException;
}
