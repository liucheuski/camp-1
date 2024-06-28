package liucheuski.siarhei.ai_summer_camp_2024_task_1.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@UtilityClass
public class FileUtil {
    public static final String OGA = ".oga";

    public File getOgaFile(File file) throws IOException {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), OGA);
        Files.write(tempFile.toPath(), Files.readAllBytes(file.toPath()));
        return tempFile;
    }
}
