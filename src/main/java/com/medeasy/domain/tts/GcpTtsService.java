package com.medeasy.domain.tts;

import com.google.cloud.texttospeech.v1.*;
import com.medeasy.common.error.TtsErrorCode;
import com.medeasy.common.exception.ApiException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
@Service
public class GcpTtsService implements TtsService{

    private final TextToSpeechClient client;
    private final String ttsOutputDir;

    public GcpTtsService(
            TextToSpeechClient client,
            @Value("${tts.output-dir:tts-mp3}") String ttsOutputDir
    ) {
        this.client = client;
        this.ttsOutputDir = ttsOutputDir;
    }

//    @PostConstruct
    public void test() {
        try  {
            // 1) 변환할 텍스트 설정
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText("안녕하세요, Google Cloud Text-to-Speech 예제입니다.")
                    .build();

            // 2) 음성 옵션 선택 (언어, 성별 등)
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("ko-KR")
                    .setSsmlGender(SsmlVoiceGender.MALE)
                    .build();

            // 3) 출력 오디오 포맷 설정
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // 4) TTS 요청 및 응답 처리
            SynthesizeSpeechResponse response =
                    client.synthesizeSpeech(input, voice, audioConfig);

            // --- 2) 저장 디렉터리 준비 ---
            Path outputDir = Paths.get(ttsOutputDir);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            // --- 3) 순차 파일명 생성 ---
            long existingCount = Files.list(outputDir)
                    .filter(p -> p.getFileName().toString().endsWith(".mp3"))
                    .count();
            String fileName = String.format("output-%d.mp3", existingCount + 1);
            Path outputFile = outputDir.resolve(fileName);

            // --- 4) 파일 쓰기 ---
            try (OutputStream out = Files.newOutputStream(outputFile, StandardOpenOption.CREATE_NEW)) {
                out.write(response.getAudioContent().toByteArray());
                System.out.println("생성 완료: " + outputFile.toAbsolutePath());
            }

        } catch (IOException e) {
                throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] convertTextToSpeech(String text) {

        // 반환 텍스트 설정
        SynthesisInput input = SynthesisInput.newBuilder()
                .setText(text)
                .build();

        // 음성 옵션 선택 (언어, 성별 등)
        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("ko-KR")
                .setSsmlGender(SsmlVoiceGender.MALE)
                .build();

        // 출력 오디오 포맷 설정
        AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build();

        SynthesizeSpeechResponse response;

        try {
            response = client.synthesizeSpeech(input, voice, audioConfig);
        }catch (Exception e){
            throw new ApiException(TtsErrorCode.GCP_TTS_REQUEST_ERROR);
        }

        return response.getAudioContent().toByteArray();
    }
}
