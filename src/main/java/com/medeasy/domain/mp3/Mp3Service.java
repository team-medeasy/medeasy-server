package com.medeasy.domain.mp3;

import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.exception.ApiException;
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

    public File createMp3File(byte[] audioBytes, String fileName) {

        try {
            Path dir = Paths.get(ttsOutputDir);

            if (Files.notExists(dir)) {
                Files.createDirectories(dir);
            }

            Path filePath = dir.resolve(fileName);
            Files.write(filePath, audioBytes, StandardOpenOption.CREATE_NEW);

            return filePath.toFile();
        } catch (IOException e) {
            throw new ApiException(ErrorCode.SERVER_ERROR, "음성 파일 생성 중 오류 발생");
        }
    }
}
