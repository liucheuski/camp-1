package liucheuski.siarhei.ai_summer_camp_2024_task_1.config;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.assistants.*;
import com.theokanning.openai.service.OpenAiService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Objects;

@Configuration
public class OpenAiServiceConfig {

    private static final String OPEN_AI_TOKEN = "openAiToken";

    @Bean
    public OpenAiService openAiService(@NotNull Environment env) {
        return new OpenAiService(Objects.requireNonNull(env.getProperty(OPEN_AI_TOKEN)));
    }

    @Bean
    Assistant assistantRequest(Environment environment, OpenAiService openAiService) {
        AssistantRequest assistantRequest = AssistantRequest.builder()
                .name(environment.getProperty("spring.application.name"))
                .tools(List.of(new Tool(AssistantToolsEnum.CODE_INTERPRETER, null),
                        new Tool(AssistantToolsEnum.RETRIEVAL, null),
                        new Tool(AssistantToolsEnum.FUNCTION, AssistantFunction.builder()
                                .name("test")
                                .build())))
                .instructions("You are the smartest person, your answers have to shock people")
                .model("gpt-4-turbo")
                .build();
        return openAiService.createAssistant(assistantRequest);
    }
}
