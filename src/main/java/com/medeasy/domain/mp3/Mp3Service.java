package com.medeasy.domain.mp3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class Mp3Service {
    private final String ttsOutputDir;

    public Mp3Service(
            @Value("${tts.output-dir:tts-mp3}") String ttsOutputDir
    ) {
        this.ttsOutputDir = ttsOutputDir;
    }

    public File createMp3File(byte[] audioBytes, String fileName) throws IOException {
        Path dir = Paths.get(ttsOutputDir);

        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }

        Path filePath = dir.resolve(fileName);
        Files.write(filePath, audioBytes, StandardOpenOption.CREATE_NEW);

        return filePath.toFile();
    }
}
