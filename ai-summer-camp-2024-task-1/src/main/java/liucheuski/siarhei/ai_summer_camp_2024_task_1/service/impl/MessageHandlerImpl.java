package liucheuski.siarhei.ai_summer_camp_2024_task_1.service.impl;

import com.theokanning.openai.ListSearchParameters;
import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.audio.CreateSpeechRequest;
import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.audio.CreateTranslationRequest;
import com.theokanning.openai.audio.TranscriptionResult;
import com.theokanning.openai.messages.MessageContent;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.Run;
import com.theokanning.openai.runs.RunCreateRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.Thread;
import com.theokanning.openai.threads.ThreadRequest;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.dao.model.Customer;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.dao.repository.CustomerRepository;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.exception.NoMessageException;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.service.MessageHandler;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.util.LanguageUtil;
import lombok.RequiredArgsConstructor;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class MessageHandlerImpl implements MessageHandler {
    private static final String OGA = ".oga";
    private final OpenAiService openAiService;
    private final CustomerRepository customerRepository;
    private final Assistant assistant;


    @Override
    public void handle(Update upd, TelegramLongPollingBot bot) throws NoMessageException, TelegramApiException, IOException {
        Message message = Optional.ofNullable(upd.getMessage())
                .orElseThrow(() -> new NoMessageException("No message"));
        if (message.hasVoice()) {
            String fileId = message.getVoice().getFileId();
            org.telegram.telegrambots.meta.api.objects.File tgFile = bot.execute(GetFile.builder()
                    .fileId(fileId)
                    .build());
            File file = bot.downloadFile(tgFile);
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), OGA);
            Files.write(tempFile.toPath(), Files.readAllBytes(file.toPath()));
            TranscriptionResult text = openAiService.createTranscription(CreateTranscriptionRequest.builder()
                    .language(LanguageUtil.getISO_639_1Code(upd))
                    .model("whisper-1")
                    .build(), tempFile);
            AtomicReference<Thread> atomicThread = new AtomicReference<>();
            customerRepository.findById(message.getChatId())
                    .ifPresentOrElse(customer -> atomicThread.set(openAiService.retrieveThread(customer.treadId())),
                            () -> {
                                atomicThread.set(openAiService.createThread(ThreadRequest.builder().build()));
                                customerRepository.save(new Customer(message.getChatId(), atomicThread.get().getId()));
                            });
            Run run = openAiService.createRun(atomicThread.get().getId(), RunCreateRequest.builder()
                    .assistantId(assistant.getId())
                    .instructions(text.getText())
                    .build());
            do {
                run = openAiService.retrieveRun(atomicThread.get().getId(), run.getId());
            } while (!"completed".equals(run.getStatus()));
            OpenAiResponse<com.theokanning.openai.messages.Message> messageOpenAiResponse = openAiService.listMessages(atomicThread.get().getId(),
                    ListSearchParameters.builder()
                            .order(ListSearchParameters.Order.DESCENDING)
                            .build());
            String answer = messageOpenAiResponse.getData().get(0).getContent().get(0).getText().getValue();
            ResponseBody rs = openAiService.createSpeech(CreateSpeechRequest.builder()
                    .model("tts-1")
                    .voice("nova")
                    .responseFormat("aac")
                    .input(answer)
                    .build());
            bot.execute(SendAudio.builder()
                    .chatId(message.getChatId())
                    .audio(new InputFile(new ByteArrayInputStream(rs.bytes()), UUID.randomUUID() + "aac"))
                    .build());
        } else {
            ResponseBody rs = openAiService.createSpeech(CreateSpeechRequest.builder()
                    .model("tts-1")
                    .voice("nova")
                    .responseFormat("aac")
                    .input("Sorry, but I understand the only voice messages. Try it out, please")
                    .build());
            bot.execute(SendAudio.builder()
                    .chatId(message.getChatId())
                    .audio(new InputFile(new ByteArrayInputStream(rs.bytes()), UUID.randomUUID() + "aac"))
                    .build());
        }
    }
}
