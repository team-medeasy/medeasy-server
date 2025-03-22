package com.medeasy.common.logging;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LogMonitor {
    private static final Logger log = LoggerFactory.getLogger(LogMonitor.class);
    @Value("${spring.mail.username}")
    private String sendToEmail;

    @Value("${spring.mail.username}")
    private String sendFromEmail;
    private final JavaMailSender mailSender;

    @Scheduled(fixedDelay = 6000000)
    public void checkLogFile() throws IOException {
        log.info("오류 이메일 알림 스케줄러 작동");

        Path logPath = Paths.get("logs/app.log");
        if (!Files.exists(logPath)) return;

        List<String> lines = Files.readAllLines(logPath);
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");


        for (String line : lines) {
            try {
                // 로그 시작이 날짜로 되어 있다고 가정
                String timestampStr = line.substring(0, 23); // "yyyy-MM-dd HH:mm:ss.SSS" 길이 = 23
                LocalDateTime logTime = LocalDateTime.parse(timestampStr, formatter);

                if (logTime.isAfter(oneHourAgo)) {
                    if (line.contains("redis refresh token 저장 오류 발생")) {
                        log.info("오류 포착 이메일 전송: {}", line);
                        sendEmail(line);
                        break;
                    }
                }
            } catch (Exception e) {
                // 로그 형식이 아니면 skip
            }
        }
    }

    public void sendEmail(String logMessage) {
        // JavaMailSender로 알림 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sendToEmail);              // 📥 수신자 이메일
        message.setFrom(sendFromEmail);         // 📤 발신자 이메일
        message.setSubject("[ERROR 발생] Spring 로그 알림");
        message.setText("🚨 로그에서 아래와 같은 에러가 감지되었습니다:\n\n" + logMessage);

        mailSender.send(message);
    }
}

