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

        // 2) 실제 저장할 Path 결정
        Path filePath = dir.resolve(fileName);

        // 3) 파일이 이미 있으면 덮어쓰거나 에러 처리할지 결정 (여기서는 신규 생성)
        Files.write(filePath, audioBytes, StandardOpenOption.CREATE_NEW);

        // 4) JVM 종료 시 파일 자동 삭제 옵션 (필요 시)
        filePath.toFile().deleteOnExit();

        return filePath.toFile();
    }
}
