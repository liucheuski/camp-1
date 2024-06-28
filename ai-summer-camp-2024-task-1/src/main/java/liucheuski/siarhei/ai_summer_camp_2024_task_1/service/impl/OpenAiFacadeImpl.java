package liucheuski.siarhei.ai_summer_camp_2024_task_1.service.impl;

import com.theokanning.openai.ListSearchParameters;
import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.audio.CreateSpeechRequest;
import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.runs.Run;
import com.theokanning.openai.runs.RunCreateRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.Thread;
import com.theokanning.openai.threads.ThreadRequest;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.dao.model.Customer;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.dao.repository.CustomerRepository;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.enums.AudioFormat;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.enums.Model;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.enums.Voice;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.service.OpenAiFacade;
import liucheuski.siarhei.ai_summer_camp_2024_task_1.util.LanguageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class OpenAiFacadeImpl implements OpenAiFacade {
    public static final String COMPLETED = "completed";
    private final OpenAiService openAiService;
    private final CustomerRepository customerRepository;
    private final Assistant assistant;

    @Override
    public String speechIntoText(File file, Update update) {
        return openAiService.createTranscription(CreateTranscriptionRequest.builder()
                .language(LanguageUtil.getISO_639_1Code(update))
                .model(Model.WHISPER.getName())
                .build(), file).getText();
    }

    @Override
    public Thread getThread(long chatId) {
        AtomicReference<Thread> atomicThread = new AtomicReference<>();
        customerRepository.findById(chatId)
                .ifPresentOrElse(customer -> atomicThread.set(openAiService.retrieveThread(customer.treadId())),
                        () -> {
                            atomicThread.set(openAiService.createThread(ThreadRequest.builder().build()));
                            customerRepository.save(new Customer(chatId, atomicThread.get().getId()));
                        });
        return atomicThread.get();
    }

    @Override
    public String getAnswer(String threadId, String question) {
        Run run = openAiService.createRun(threadId, RunCreateRequest.builder()
                .assistantId(assistant.getId())
                .instructions(question)
                .build());
        do {
            run = openAiService.retrieveRun(threadId, run.getId());
        } while (!COMPLETED.equals(run.getStatus()));
        OpenAiResponse<Message> messageOpenAiResponse = openAiService.listMessages(threadId,
                ListSearchParameters.builder()
                        .order(ListSearchParameters.Order.DESCENDING)
                        .build());
        return messageOpenAiResponse.getData().get(0).getContent().get(0).getText().getValue();
    }

    @Override
    public byte[] textIntoSpeech(String answer) throws IOException {
        return openAiService.createSpeech(CreateSpeechRequest.builder()
                .model(Model.TTS.getName())
                .voice(Voice.NOVA.getName())
                .responseFormat(AudioFormat.AAC.getName())
                .input(answer)
                .build()).bytes();
    }
}
