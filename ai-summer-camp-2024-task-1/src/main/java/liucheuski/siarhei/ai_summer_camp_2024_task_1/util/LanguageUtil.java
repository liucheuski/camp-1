package liucheuski.siarhei.ai_summer_camp_2024_task_1.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Locale;
import java.util.Optional;

@UtilityClass
public class LanguageUtil {

    private static final String RU = "ru";

    public String getISO_639_1Code(Update update) {
        String languageIETFTag = Optional.ofNullable(update)
                .map(Update::getMessage)
                .map(Message::getFrom)
                .map(User::getLanguageCode)
                .orElse(RU);
        return Locale.forLanguageTag(languageIETFTag).getLanguage();
    }
}
