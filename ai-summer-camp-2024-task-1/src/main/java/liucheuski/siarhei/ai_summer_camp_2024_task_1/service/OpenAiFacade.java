package liucheuski.siarhei.ai_summer_camp_2024_task_1.service;

import com.theokanning.openai.threads.Thread;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;

public interface OpenAiFacade {

    String speechIntoText(File file, Update update);

    Thread getThread(long chatId);

    String getAnswer(String threadId, String question);

    byte[]  textIntoSpeech(String answer) throws IOException;
}
