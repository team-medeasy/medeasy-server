package com.medeasy.domain.file;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcpFileBucketService implements FileBucketService {

    private final Storage storage;

    @Override
    public String saveAudioFile(byte[] audioBytes, String bucketName, String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("audio/mpeg")
                .build();
        storage.create(blobInfo, audioBytes);

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }

    @Override
    public String getAudioFileUrl(String bucketName, String fileName) {
        // 스토리지에 파일이 존재하는지 조회
        Blob blob = storage.get(BlobId.of(bucketName, fileName));
        if (blob != null && blob.exists()) {
            log.info("GCS에 이미 존재하는 파일입니다: {}", fileName);

            return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
        }
        return null;
    }
}
